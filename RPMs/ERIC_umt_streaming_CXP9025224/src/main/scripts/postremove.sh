#!/bin/bash

echo "RPM Postremove"

rm -f /etc/ericsson/config/umt/pmstream.ini
rm -f /etc/ericsson/config/umt/pmstream.conf