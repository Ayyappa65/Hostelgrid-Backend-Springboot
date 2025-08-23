#!/bin/bash
echo "Stopping all HostelGrid services..."

echo "Finding and killing Spring Boot processes..."
pkill -f "spring-boot:run"
pkill -f "maven"
pkill -f "java.*spring-boot"

echo "Waiting for processes to terminate..."
sleep 3

echo "Checking for remaining Java processes..."
ps aux | grep -E "(spring-boot|maven)" | grep -v grep | awk '{print $2}' | xargs -r kill -9

echo "All services stopped."
echo "You can now restart services if needed."