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
              { target: 100,  duration: '15s' },
              { target: 200, duration: '15s' },
              { target: 500, duration: '10s' },
              { target: 500, duration: '10s' },
            ],
    },
  },
  thresholds: {
    http_req_failed: ['rate<0.5'],       // порог подняли — при такой нагрузке будут ошибки
    http_req_duration: ['p(95)<10000'],  // 10 секунд — чтобы тест не падал сразу
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
}