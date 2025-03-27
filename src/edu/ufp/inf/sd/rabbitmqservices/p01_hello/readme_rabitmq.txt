#======================== Install rabbitmq: ========================
#Before installing/upgrading make sure the taps are up-to-date:
$ brew update

# Find paths where rabbitmq directories exist
$ find / -type d -name "rabbitmq" -print 2>/dev/null

/usr/local/etc/rabbitmq
/usr/local/var/lib/rabbitmq
/usr/local/var/log/rabbitmq
/usr/local/Cellar/rabbitmq

$ brew services stop rabbitmq
$ brew uninstall rabbitmq
$ rm -rf /usr/local/etc/rabbitmq
$ rm -rf /usr/local/var/lib/rabbitmq
$ rm -rf /usr/local/var/log/rabbitmq
$ rm -rf /usr/local/Cellar/rabbitmq

$ brew install rabbitmq
$ brew install rabbitmq

$ brew upgrade rabbitmq

#======================== Location and Starting rabbitmq: ========================
# To find out locations anf how to launch:
$ brew info rabbitmq

# Installed...
/usr/local/Cellar/rabbitmq/4.0.7

# To start rabbitmq now and restart at login (as service):
$ brew services start rabbitmq
# Or
# To start on shell:
$  CONF_ENV_FILE="/usr/local/etc/rabbitmq/rabbitmq-env.conf" /usr/local/opt/rabbitmq/sbin/rabbitmq-server

# Several commands available here:
$ cd /usr/local/opt/rabbitmq/sbin

# Enable all feature flags after installation (get access to certain features and release compatibility):
$ /usr/local/opt/rabbitmq/sbin/rabbitmqctl enable_feature_flag all

# Config file:
$ more /usr/local/etc/rabbitmq/rabbitmq-env.conf

# Management UI (guest/guest):
http://localhost:15672


#Both RabbitMQ server scripts and CLI tools are installed into the sbin directory under:
#   /usr/local/Cellar/rabbitmq/{version}/ for Intel Macs
#   /usr/local/opt/rabbitmq/sbin for for Intel Macs
#   /usr/local/sbin for Intel Macs
#or
#   /opt/homebrew/Cellar/rabbitmq/{version}/ for Apple Silicon Macs.
#   /opt/homebrew/opt/rabbitmq/sbin for Apple Silicon Macs
#   /opt/homebrew/sbin for Apple Silicon ones.