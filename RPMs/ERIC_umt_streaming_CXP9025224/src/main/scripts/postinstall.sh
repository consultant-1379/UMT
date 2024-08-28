#!/bin/bash

mkdir -p /var/ericsson/log/umt/
mkdir -p /var/ericsson/umt/dumps

if [ ! -f /etc/ericsson/config/umt/pmstream.conf ]; then
    ln -s -f /etc/ericsson/config/umt/default-pmstream.conf /etc/ericsson/config/umt/pmstream.conf
fi

if [ ! -f /etc/ericsson/config/umt/pmstream.ini ]; then
    ln -s -f /etc/ericsson/config/umt/default-pmstream.ini /etc/ericsson/config/umt/pmstream.ini
fi

AGENT_PROPS=/opt/hyperic/hyperic-hqee-agent/conf/agent.properties
sed "s/\(autoinventory.runtimeScan.interval.millis=\)86400000/\1900000/g" ${AGENT_PROPS} >${AGENT_PROPS}.orig
rm -f ${AGENT_PROPS}
cp -f ${AGENT_PROPS}.orig ${AGENT_PROPS}
chown hyperic:hyperic ${AGENT_PROPS}
chmod 755 ${AGENT_PROPS}

service hyperic-agent restart


echo "RPM Postinstall"
exit 0

