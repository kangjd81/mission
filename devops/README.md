DevOps 사전과제 
=====
### 요건
- build script는 gradle로 작성
- Container 로 구성
- Log 는 Host 에 file 로 적재
- Container scale in/out 가능
- 무중단 배포 동작을 구현 (배포 방식에 제한 없음)
- 웹서버는 Reverse proxy 80 port, Round robin 방식
- 실행스크립트 작성 (bash/python/go 중 선택)
- 어플리케이션 REST API 추가
    [GET /health] Health check - 응답결과는 JSON Object
- README.md 파일에 프로젝트 실행 방법 명시



### 사전 설치 도구
- docker / docker-compose  
- git
- ~~jdk1.8~~


---
### 실행 방법


#### 1. 프로젝트 다운로드
소스 git clone

```
# git clone https://github.com/kangjd81/mission.git
# cd mission/devops
```

#### 2. 빌드 
host장비에 jdk 설치 이후

```
# sh devops.sh build 
```

> **참고)**
> jdk 설치가 되지 않는 환경에서는 build 컨테이너 이용
> 
>> ``` 
>> # docker-compose up -d --build gradle
>> ```
>
> 빌드 대기 (1분 정도 소요) 후 파일 확인
>> ```
>> # docker-compose exec gradle ls -al /home/gradle/build/libs/sample-web.jar
>> -rw-r--r--    1 gradle   gradle    18266795 May  6 14:10 /home/gradle/build/libs/sample-web.jar
>> ```
>
> container->host로 파일 복사
>> ```
>> # mkdir -p ./build/libs
>> # docker ps | grep gradle
>> (containerID 확인)
>> # docker cp {containerID}:/home/gradle/build/libs/sample-web.jar ./build/libs/
>> ```
>
> build 컨테이너 제거
>> ```
>> # docker-compose down
>> ```
		

#### 3. 실행 

서비스 실행 (nginx, app_v1)
```
# sh devops.sh start
```
> [서비스 체크](https://github.com/kangjd81/mission/tree/master/devops#4-%EC%84%9C%EB%B9%84%EC%8A%A4-%EC%B2%B4%ED%81%AC)

서비스 재시작
```
# sh devops.sh restart
```

서비스 배포 (app_v2)
```
# sh devops.sh deploy
``` 
> 배포 이후 스크립트에서 실제 서비스 호출 시 app_v2 응답 확인 후에 app_v1 종료 처리 진행

스케일 인/아웃
```
# sh devops.sh scale 3
```
> nginx는 디폴트로 Round robin 방식.

서비스 중지
```
# sh devops.sh stop
```


   
#### 4. 서비스 체크
```
curl -H "Host: app.host" localhost/health   
or 
curl app.127.0.0.1.xip.io/health   
```


#### 5. 로그 확인
```
tail -f build/libs/logs/logfile.log
```



---
### 기타


#### /health JSON 구성
```json
{
	"appID": "sH1oW1PPIowj5gA",  
	"version": "1.0",            
	"status": "up"               
}
```
> appID: 컨테이너에 배포된 고유한 어플리케이션의 ID  
> version : 어플리케이션 버전  
> status : 서비스 상태  


#### 어플리케이션 버전업
runnable jar 구동 시 파라미터로 app version 명시  
```java -jar sample-web.jar --app.version=2.0```



#### 무중단 배포
- nginx Reverse proxy기반의 동적 upstream 설정 변경 방식
- [docker-gen](https://github.com/jwilder/docker-gen)을 활용하여 nginx 프록시 구성 생성 자동화
- [nginx-proxy](https://hub.docker.com/r/jwilder/nginx-proxy) image 사용 (nginx + docker-gen 실행)


#### 실습 환경 
- [katacoda - ubuntu/playground](https://www.katacoda.com/courses/ubuntu/playground)
- [play-with-docker](https://labs.play-with-docker.com/)



