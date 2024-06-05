# Partage API 서비스

Partage API 서비스는 Partage 프로젝트의 백엔드 서비스입니다.

## 요구 사항

- Docker
- Docker Compose

## 주의 사항
- docker compose 명령어 실행시 환경에 따라 `docker compose` 또는 `docker-compose` 명령어를 사용합니다.
- 아래 명령어는 프로젝트 루트 디렉토리에서 실행합니다.

## 1. 도커 이미지 생성 & 컨테이너 배포

처음 실행할 때는 다음 명령어를 실행하여 서비스를 빌드하고 컨테이너를 배포합니다.


### `.env` 파일 사용하지 않는 경우
```sh
KEY=your-encryption-key docker compose up -d
```

또는

### `.env` 파일 사용

프로젝트 루트 디렉토리에 `.env` 파일을 생성하고 다음 내용을 추가합니다.

```sh
KEY=your-encryption-key
```

이후 다음 명령어를 실행하여 서비스를 빌드하고 컨테이너를 배포합니다.

```sh
docker compose up -d
```

### 구동 확인

다음 명령어를 실행하여 서비스가 정상적으로 구동되었는지 확인합니다.

```sh
docker compose ps
```

### API 서버 접속

`http://localhost:9090/api/health` 에 접속하여 API 서버가 정상적으로 동작하는지 확인합니다.

## 2. 서비스 로그 확인

서비스 로그는 다음 명령어를 사용하여 확인할 수 있습니다.

### 로그 확인
```sh
docker logs partage
```
또는 log tailing

```sh
docker logs -f --tail 100 partage
```


## 3. 서비스 실행 & 중지 & 재시작

도커 이미지 생성 및 컨테이너 배포 이후에는 다음 명령어를 사용하여 서비스를 실행, 중지, 재시작할 수 있습니다.
(1번 명령어 실행시 서비스는 실행 상태로 전환됩니다.)

### 서비스 실행
```sh
docker start partage
```

### 서비스 중지
```sh
docker stop partage
```

### 서비스 재시작
```sh
docker restart partage
```

## 4. 도커 컨테이너 및 이미지 삭제

### 서비스 삭제
```sh
docker compose down -v
```
### 이미지 삭제
```sh
docker rmi partage
```