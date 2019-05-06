#!/bin/sh

# 서비스 시작
start() {
	docker-compose up --build -d nginx-gen app1
	echo "###### Starting Docker Application ######"
}

# 서비스 중지
stop() {
	docker-compose stop nginx-gen app1 app2
	echo "###### Stop Docker Application ######"
}

# 서비스 v2.0 배포
deploy() {
	docker-compose up --build -d app2
	echo "###### Deploy Application (v2.0) ######"
	
	echo "Checking Deploy Service..."
	while true
	do
		VER=$(ver_check)
		if [ $VER = "2.0" ]
		then
			echo "Check Success"
			docker-compose stop app1
			break
		else
			echo "Checking..."
		fi

		sleep 1
	done
}

# 스케일 업/다운
scale() {
	VER=$(ver_check)
	if [ $VER = "1.0" ]
	then
		docker-compose scale app1=$1
	else
		docker-compose scale app2=$1
	fi
}

# 배포버전 체크
ver_check() {
	RES_STR=$(curl -s -H "Host: app.host" localhost/health)
	VERSION=$(echo $RES_STR | sed -n 's|.*"version":"\([^"]*\)".*|\1|p')
	echo $VERSION
}

case "$1" in
	build)
		gradle clean bootjar
	;;
	start)
		start
	;;
	stop)
		stop
	;;
	restart)
        stop
        sleep 3
        start
	;;
    deploy)
        deploy
    ;;
    scale)
        if [ -z "$2" ]
        then
                echo "Scale in/out size not entered"
                exit 1;
        fi
        scale $2
    ;;
    *)
    echo $"Usage: $0 {build|start|stop|restart|scale|deploy}"
    exit 1
esac
