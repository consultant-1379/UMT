#!/bin/bash

rmdir -p /var/ericsson/log/umt/ 2>/dev/null || echo "Not removing non empty log directory"

echo "RPM Preremove"

