#!/bin/bash
echo "Stopping all services..."
pkill -f "mailhog" 2>/dev/null && echo "✓ MailHog stopped" || echo "✗ MailHog not running"
pkill -f "smspitt" 2>/dev/null && echo "✓ SMSPitt stopped" || echo "✗ SMSPitt not running"
pkill -f "notification-service" 2>/dev/null && echo "✓ Notification-service stopped" || echo "✗ Notification-service not running"
pkill -f "bug_reporter" 2>/dev/null && echo "✓ Backend stopped" || echo "✗ Backend not running"
echo ""
echo "Done."
