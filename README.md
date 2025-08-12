# News Intelligent
![ni-readme-img](https://github.com/user-attachments/assets/c7fdffcb-6808-42f8-9f05-be84b1379fd4)

<br />

## 👩🏻‍💻 팀원 소개

| <img src="https://avatars.githubusercontent.com/u/118096607?v=4" width="150" height="150"/> | <img src="https://avatars.githubusercontent.com/u/101035453?v=4" width="150" height="150"/> | <img src="https://avatars.githubusercontent.com/u/180490320?v=4" width="150" height="150"/> | <img src="https://avatars.githubusercontent.com/u/151008307?v=4" width="150" height="150"/> | <img src="https://avatars.githubusercontent.com/u/167287284?v=4" width="150" height="150"/> |
| :-----------------------------------------------------------------------------------------: | :-----------------------------------------------------------------------------------------: | :-----------------------------------------------------------------------------------------: | :----------------------------------------------------------------------------------------: | :-----------------------------------------------------------------------------------------: |
|                 서가영<br/>[@caminobelllo](https://github.com/caminobelllo)                 |                       서동혁<br/>[@weeast1521](https://github.com/weeast1521)                       |                이윤서<br/>[@yoonseo8167](https://github.com/yoonseo8167)                |                     강수진<br/>[@nnmrm](https://github.com/nnmrm)                     |                    신윤진<br/>[@nomad1jin](https://github.com/nomad1jin)                    | 

<br />

## 💡 기술 스택
- Java 17
- Spring Boot 3.5.3
- Spring Data JPA
- MySQL 8.0.36
- Docker, DockerHub
- GitHub Actions
- AWS EC2, AWS RDS, AWS S3
- Prometheus, Grafana
  
<br />

## 📋 ERD
<img width="1770" height="1122" alt="news_erd (2)" src="https://github.com/user-attachments/assets/4a98ac49-6043-4986-bb47-5d1230679cb0" />


<br />

## ☁️ 시스템 아키텍처
<img width="1577" height="1144" alt="ni-backend-architechture drawio" src="https://github.com/user-attachments/assets/172bf97f-fcc8-4c9a-810e-0b73264f9e0a" />

<br />

## 🌱 브랜치 전략
- `develop`
- `main`
- `feature/기능명` or `feature/#관련이슈번호`

<br />

## 🖍️ Issue 
- Feature : 기능 개발 시
- Refactor : 코드 개선 시
- Bug : 오류 수정 시

<br />

## 📂 폴더 구조
```
📂 backend
├── 📂 domain
│    ├── 📂 member
│    │   ├── 📂 controller
│    │   ├── 📂 converter
│    │   ├── 📂 dto
│    │   ├── 📂 entity
│    │   ├── 📂 repository
│    │   ├── 📂 service
│    ├── 📂 news
│    ├── 📂 topic
│    ├── 📂 newsCategory
│    └── 📂 ..
│
├── 📂 global
│    ├── 📂 apiPayload
│    │   ├── 📂 code
│    │   |   ├── 📂 error
│    │   |   ├── 📂 success
│    │   ├── 📂 exception
│    │   |   ├── 📂 handler
│    │   |   ├── CustomException
│    ├── 📂 config
│    │   |   ├── 📂 properties
│    │   |   ├── 📂 security
│    │   |   ├── 📂 ..
│    ├── 📂 entity
│    └──   |   ├── BaseEntity 
```

