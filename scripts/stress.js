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
      preAllocatedVUs: 500,
      maxVUs: 500,
      exec: 'sendRequests',
      stages: [
              { target: 500,  duration: '30s' },
              { target: 500, duration: '30s' },
              { target: 500, duration: '30s' },
              { target: 500, duration: '30s' },
            ],
    },
  },
  thresholds: {
    http_req_failed: ['rate<0.5'],
    http_req_duration: ['p(95)<10000'],
  },
};

export function sendRequests() {
  const res = http.post('http://localhost:8080/api/process', JSON.stringify({ message: 'Hello world' }), {
      headers: { 'Content-Type': 'application/json' },
      tags: { variant: 'mvc-http' },
  });

  const success = check(res, {
      'status is 200': (r) => r.status === 200,
  });

  if (!success) {
      return;
  }

  const uuid = res.json('uuid');

  const getRes = http.get(`http://localhost:8080/api/process/${uuid}`, {
      tags: { variant: 'mvc-http-get' },
  });

  check(getRes, {
      'get status is 200': (r) => r.status === 200,
  });
}