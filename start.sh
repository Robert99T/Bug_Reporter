#!/bin/bash
set -e

echo "Starting all services..."
echo ""

# Start MailHog
nohup /tmp/mailhog > /tmp/mailhog.log 2>&1 &
echo "✓ MailHog (email) → http://localhost:8025"

# Start SMSPitt
nohup npx smspitt > /tmp/smspitt.log 2>&1 &
echo "✓ SMSPitt (SMS) → http://localhost:2875"

# Start notification-service
cd notification-service
nohup mvn spring-boot:run -q > /tmp/notification-service.log 2>&1 &
echo "✓ Notification-service → http://localhost:8081"
cd ..

# Start backend
cd backend
nohup mvn spring-boot:run -q > /tmp/backend.log 2>&1 &
echo "✓ Backend → http://localhost:8080"
cd ..

echo ""
echo "All services started. Check logs in /tmp/*.log"
