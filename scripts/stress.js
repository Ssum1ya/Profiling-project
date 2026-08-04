import http from 'k6/http';
import { check, sleep } from 'k6';

const VARIANT = __ENV.VARIANT;

export const options = {
  tags: { variant: VARIANT },
  scenarios: {
    ramping_rps: {
      executor: 'ramping-arrival-rate',
      startRate: 50,
      timeUnit: '1s',
      preAllocatedVUs: 100,
      maxVUs: 500,
      exec: 'sendRequests',
      stages: [
              { target: 100,  duration: '30s' },
              { target: 150, duration: '30s' },
              { target: 250, duration: '30s' },
              { target: 500, duration: '30s' },
            ],
    },
  }
};

export function sendRequests() {
  const res = http.post('http://localhost:8080/api/process', JSON.stringify({ message: 'Hello world' }), {
      headers: { 'Content-Type': 'application/json' },
      tags: { variant: 'mvc-http' },
  }); // запрос на 1 ендпойнт

  const success = check(res, {
      'status is 200': (r) => r.status === 200,
  });

  // если 1 запрос провалился, мы не делаем 2 запрос
  if (!success) {
      return;
  }

  const uuid = res.json('uuid');

  const getRes = http.get(`http://localhost:8080/api/process/${uuid}`, {
      tags: { variant: 'mvc-http-get' },
  }); // запрос на 2 ендпойнт

  check(getRes, {
      'get status is 200': (r) => r.status === 200,
  });
}
