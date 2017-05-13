#!/bin/bash

#Check system information - cloud 
echo "Checking system information..."
# Declare array
declare -a INFO
INFO=( $(cat /etc/*-release) )
#for (( i=0; i<${#INFO[@]}; i++ )); do echo ${INFO[i]}; done
equals="="
if  [ "${INFO[0]/$equals}" = "${INFO[0]}" ] ; then
  DISTRO=${INFO[0]}    
  #echo "${equals} is not in ${INFO[0]}"
  
else
  #echo "${equals} was found in ${INFO[0]}"
  #saveIFS=$IFS
  #IFS='=' #read -ra DISTROTEST <<< "${INF0[0]}"
  #DISTRO=(${INFO[0]})
  #DISTRO=${DISTRO[@]:-1}
   DISTRO=${INFO[0]##*=}
fi
DISTRO=$(echo $DISTRO | cut -c 2-)
echo "You are using $DISTRO Linux Distro"
echo "Updating your distro"
case "${DISTRO^^}" in
        DEBIAN)
#             tar zcvf etc.backup.tar.gz /etc/
#             x="$(dpkg --list | grep php | awk '/^ii/{ print $2}')"
#             apt-get --purge remove $x
#             echo "deb http://packages.dotdeb.org jessie all" >> /etc/apt/sources.list
#             echo "deb-src http://packages.dotdeb.org jessie all" >> /etc/apt/sources.list
#             cd /tmp
#             wget https://www.dotdeb.org/dotdeb.gpg
#             apt-key add dotdeb.gpg
#             rm dotdeb.gpg
             apt update && apt upgrade -y
                ;;
esac
#exit;
#exit 1
#Check pre-requisites
#PHP

#load pre-requisites in array and compare to results of the following
#MySQL
echo "Checking Apache..."
case "${DISTRO^^}" in
        UBUNTU)
	    APACHE_INST=( $(dpkg -l apache2) )
	    count=${#APACHEL_INST[@]}
	    #echo "COUNT $count"
	    if [ "${count}" -eq 0 ]; then
	      echo "Installing Apache.."
	      sudo apt-get install -y apache2 libapache2-mod-php
	    else 
	      echo "Apache installed"
	    fi
            ;;

	DEBIAN)

	    APACHE_INST=( $(dpkg -l apache2) )
	    count=${#APACHEL_INST[@]}
	    #echo "COUNT $count"
	    if [ "${count}" -eq 0 ]; then
	      echo "Installing Apache.."
	      apt-get install -y apache2 libapache2-mod-php5
	    else 
	      echo "Apache installed"
	    fi
            ;;
         
        CENTOS)
            APACHE_INST=( $(rpm -qa | grep httpd) )
	    count=${#APACHEL_INST[@]}
	    #echo "COUNT $count"
	    if [ "${count}" -eq 0 ]; then
	      echo "Installing Apache.."
	      yum install -y httpd mod_ssl
	      firewall-cmd --permanent --add-port=80/tcp
	      firewall-cmd --permanent --add-port=443/tcp
	      firewall-cmd --reload
	    else 
	      echo "Apache installed"
	    fi
            ;;
            
        OPENMANDRIVA)
	    APACHEL_INST=( $(rpm -qa | grep apache) )
	    
	    count=${#APACHEL_INST[@]}
	    #echo "COUNT $count"
	    if [ "${count}" -eq 0 ]; then
	      echo "Installing Apache.."
	    else 
	      echo "Apache installed"
	    fi
            ;;
         
        
         
        *)
            echo $"Your distro is not yet supported for schoollms"
            exit 1
 
esac
#exit 1
echo "Checking PHP pre-requisites..."
PHP_PRE=( 'php-gd' 'php-xml' 'php-mcrypt' 'php-mbstring' 'php-zip' 'php-curl' 'php-fileinfo' 'php-mysql' 'drush' )
#distro= ${INFO[0]^^}
#echo $distro
php_found=false
case "${DISTRO^^}" in
        UBUNTU)
	    PHP_INST=( $(dpkg -l php) )
	    
	    for (( i=0; i<${#PHP_INST[@]}; i++ )); 
	    do 
	      PHP_PRE=( 'php-gd' 'php-xml' 'php-mcrypt' 'php-mbstring' 'php-zip' 'php-curl' 'php-fileinfo' 'php-mysql' 'drush' ) 
	      for (( j=0; j<${#PHP_PRE[@]}; j++ ));
		do 
		  if [[ "${PHP_INST[i]}" == *"${PHP_PRE[j]}"* ]]; then
		    echo "${PHP_PRE[j]} installed as ${PHP_INST[i]}";
		    php_found=true
		  else
		    php_found=false
		    echo "'${PHP_PRE[j]}' not installed";
		  fi
		done
	     done
	    if ($php_found); then
	      echo "PHP is installed"
	    else   
	      echo "Installing php"
	      sudo apt-get install -y php php-gd php-xml php-mcrypt php-mbstring
	    fi
            ;;
        
	DEBIAN)
	    PHP_INST=( $(dpkg -l php5) )
	    
	    for (( i=0; i<${#PHP_INST[@]}; i++ )); 
	    do 
	      PHP_PRE=( 'php5-gd' 'php5-xml' 'php5-mcrypt' 'php5-mbstring' 'php5-zip' 'php5-curl' 'php5-fileinfo' 'php5-mysql' 'drush' )
	      for (( j=0; j<${#PHP_PRE[@]}; j++ ));
		do 
		  if [[ "${PHP_INST[i]}" == *"${PHP_PRE[j]}"* ]]; then
		    echo "${PHP_PRE[j]} installed as ${PHP_INST[i]}";
		    php_found=true
		  else
		    php_found=false
		    echo "'${PHP_PRE[j]}' not installed";
		  fi
		done
	     done
	    if ($php_found); then
	      echo "PHP is installed"
	    else   
	      echo "Installing php"
	       apt-get install -y php5 php5-gd php5-xml php5-mcrypt php5-mbstring php5-mysql php5-zip php5-curl php5-fileinfo drush
	    fi
            ;;
 
        CENTOS)
            PHP_INST=( $(rpm -qa | grep php) )
	    for (( i=0; i<${#PHP_INST[@]}; i++ )); 
	    do 
	      PHP_PRE=( 'php-gd' 'php-xml' 'php-mcrypt' 'php-mbstring' 'php-zip' 'php-curl' 'php-fileinfo' 'php-mysql' 'drush' ) 
	      for (( j=0; j<${#PHP_PRE[@]}; j++ ));
		do 
		  if [[ "${PHP_INST[i]}" == *"${PHP_PRE[j]}"* ]]; then
		    echo "${PHP_PRE[j]} installed as ${PHP_INST[i]}";
		    php_found=true
		  else
		    echo "'${PHP_PRE[j]}' not installed";
		    php_found=false
		  fi
		done
	     done
	     if ($php_found); then
	      echo "PHP is installed"
	    else   
	      echo "Installing php"
	      rpm -Uvh https://dl.fedoraproject.org/pub/epel/epel-release-latest-7.noarch.rpm
	      rpm -Uvh https://mirror.webtatic.com/yum/el7/webtatic-release.rpm
	      yum install -y php php-gd php-xml php-mcrypt php-mbstring php-zip php-curl php-fileinfo php-mysql drush
	    fi

            ;;
            
        OPENMANDRIVA)
	    PHP_INST=( $(rpm -qa | grep php) )
	    for (( i=0; i<${#PHP_INST[@]}; i++ )); 
	    do 
	      PHP_PRE=( 'php-gd' 'php-xml' 'php-mcrypt' 'php-mbstring' ) 
	      for (( j=0; j<${#PHP_PRE[@]}; j++ ));
		do 
		  if [[ "${PHP_INST[i]}" == *"${PHP_PRE[j]}"* ]]; then
		    echo "${PHP_PRE[j]} installed as ${PHP_INST[i]}";
		  else
		    echo "'${PHP_PRE[j]}' not installed";
		  fi
		done
	     done
            ;;
         
        
         
        *)
            echo $"Your distro is not yet supported for schoollms"
            exit 1
 
esac

#exit 1
#Drush
#echo "Checking DRUSH..."
#rpm -qa | grep drush

#MySQL
echo "Checking MySQL..."
case "${DISTRO^^}" in
        UBUNTU)
	    MYSQL_INST=( $(dpkg -l mysql) )
	    count=${#MYSQL_INST[@]}
	    #echo "COUNT $count"
	    if [ "${count}" -eq 0 ]; then
	      echo "Installing MySQL.."
	      sudo apt-get install -y mysql-server
	    else 
	      echo "MySQL installed"
	    fi
            ;;
        
	DEBIAN)
	    MYSQL_INST=( $(dpkg -l mysql) )
	    count=${#MYSQL_INST[@]}
	    #echo "COUNT $count"
	    if [ "${count}" -eq 0 ]; then
	      echo "Installing MySQL.."
	        debconf-set-selections <<< 'mysql-server-5.6 mysql-server/root_password password $0W3t0'
            debconf-set-selections <<< 'mysql-server-5.6 mysql-server/root_password_again password $0W3t0'
	        apt-get install -y mysql-server
	    else 
	      echo "MySQL installed"
	    fi
            ;;
 
        CENTOS)
            MYSQL_INST=( $(rpm -qa | grep mysql) )
	    count=${#MYSQL_INST[@]}
	    #echo "COUNT $count"
	    if [ "${count}" -eq 0 ]; then
	      echo "Installing MySQL.."
	      yum install -y wget
	      wget http://repo.mysql.com/mysql-community-release-el7-5.noarch.rpm
	      rpm -ivh mysql-community-release-el7-5.noarch.rpm
	      yum update -y
	      yum install -y mysql-server
	      systemctl start mysqld
	      mysql_secure_installation
	    else 
	      echo "MySQL installed"
	    fi
            ;;
            
        OPENMANDRIVA)
	    MYSQL_INST=( $(rpm -qa | grep mysql) )
            ;;
         
        
         
        *)
            echo $"Your distro is not yet supported for schoollms"
            exit 1
 
esac

#PROVIDE database USERNAME AND PASSWORD
#echo "Please provide MySQL username and password for root user";
#read -p 'Username: ' uservar
#read -sp 'Password: ' passva

$passva = "$0W3t0"

cat <<EOT > .mysql.cnf
[client]
password="$passva"

[mysqldump]
host=localhost
password="$passva"

[client]
host=localhost
password="$passva"
EOT

cat <<EOT > .schoolms_core.conf 
<VirtualHost *:80>
ServerAdmin modise@ekasiit.com
DocumentRoot /var/www/html/schoollmscoredevdev/
ServerName devdev.schoollms.net
ErrorLog logs/devdev.schoollms.net-error_log
CustomLog logs/devdev.schoollms.net-access_log common
</VirtualHost>
EOT

cat <<EOT > /etc/httpd/conf/httpd.conf
IncludeOptional sites-enabled/*.conf
EOT

#CREATE user schoollms
echo "Creating USER schoollms..."
USER_ADD=( $(useradd -m -p soweto schoollms) )
for (( i=0; i<${#USER_ADD[@]}; i++ )); 
do
  if [[ "${USER_ADD[i]}" == *"exist"* ]]; then
	  echo "USER schoollms exists";
       #else
	  #echo "'${PHP_PRE[j]}' not installed";
  fi
done

#CREATE folder source
echo "Creating DIRECTORY schoollms/core/devdev/source..."
mkdir -p /home/schoollms/schoollms/core/dev/dev/source

#GET INSTALL release
echo "Get INSTALL release..."
releasefile="./release/release.txt"
if [ -e $file ]; then
   cat $releasefile | while read LINE
   do
    release=$LINE
    #CREATE release version DIRECTORY
    #echo "Create release version DIRECTORY..."
    #mkdir -p /home/schoollms/schoollms/core/dev/dev/source/$release
    #CREATE INSTALL files to 
    echo "Copy RELEASE source..."
    tar xzvf ./release/$release."tar.gz" -C /home/schoollms/schoollms/core/dev/dev/source/
    #echo $release
    #CREATE INSTALL files to 
    echo "Create RELEASE database..."
    db_release=${release//"."/_}
    db_name="schoollms_dev_"$db_release"_vas_timetable"
    #echo $db_name
    mysqladmin --defaults-extra-file='.mysql.cnf' -uroot create $db_name 
    echo "Loading RELEASE data..."
    mysql --defaults-extra-file='.mysql.cnf' -uroot $db_name < ./release/$release."mysql"

    echo "Preparing links to the core for core system developers"
    ln -s /home/schoollms/schoollms/core/dev/dev/source/ /var/www/html/schoollmscoredevdev
    
    echo "Prepare apache virtual host for developers at http://devdev.schoollms.net"
 
    case "${DISTRO^^}" in
        UBUNTU)
        
	  ;;
        
        CENTOS)
          chown -R apache: /home/schoollms/schoollms/
          chown -R apache: /var/www/html/schoollmscoredevdev
	  mkdir /etc/httpd/sites-available
	  mkdir /etc/httpd/sites-enabled
 

	  mv .schoolms_core.conf  /etc/httpd/sites-available/devdev.schoollms.net.conf
	  ln -s /etc/httpd/sites-available/devdev.schoollms.net.conf /etc/httpd/sites-enabled/
	  apachectl restart
	    ;;
	  *)
            echo $"Your distro is not yet supported for schoollms"
            exit 1
 
    esac
    done
   #echo $release
else 
	echo "File does not exists"
	exit 1
fi 

 

