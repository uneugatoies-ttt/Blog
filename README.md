# 개요
## 어플리케이션의 소개
이 어플리케이션은 간단한 블로그 사이트의 기능을 수행하기 위한 REST API들을 정의합니다. 이것은 Spring Boot를 사용하는 백엔드 어플리케이션으로, 프론트엔드 어플리케이션에게 API를 제공하며, 프론트엔드로부터 들어오는 요청을 method와 URL에 따라 분류해 해당하는 API의 로직으로 처리합니다.

## 어플리케이션을 만든 목적
제가 이 어플리케이션을 만든 목적은 Spring Boot를 사용한 프로젝트를 직접 제작해 보면서 그에 대한 이해도를 높이고 개발 과정에 익숙해지기 위함입니다.

<br/>

# 기술 스택
- 언어: Java 11
- 프레임워크: Spring Boot 2.7.15
- 빌드 도구: Gradle 8.5
- ORM: JPA, Spring Data JPA
- 데이터베이스: MariaDB
- 보안: Spring Security, JWT, OAuth2
- 테스트: JUnit 5

<br/>

# 주요 기능
## 회원과 인증 관련 기능
### 일반 회원 가입, 로그인 및 회원 탈퇴
- 사용자는 닉네임, 비밀번호, 이메일을 입력해 회원 가입을 할 수 있습니다.
- 등록된 사용자에게는 자동으로 블로그가 하나 할당됩니다.
- 등록된 사용자는 닉네임과 비밀번호를 입력해 로그인을 할 수 있습니다.
- 등록된 사용자는 닉네임과 비밀번호를 입력해 회원 탈퇴를 할 수 있습니다.
### OAuth2 회원 가입 및 로그인
- Google과 GitHub의 계정을 이용해서 이 어플리케이션에서의 인증을 할 수 있습니다.

## 블로그 게시물 관련 기능
### 게시물 작성, 수정 및 삭제
- 등록된 사용자는 자신의 블로그에서 게시물 작성, 수정 및 삭제를 할 수 있습니다.
### 게시물 열람
- 등록된 혹은 등록되지 않은 사용자는, 자신 혹은 타인의 블로그에서 해당하는 게시물을 열람할 수 있습니다.

## 카테고리 관련 기능
### 카테고리 추가, 명칭 수정 및 삭제
- 등록된 사용자는 자신의 블로그에서 카테고리 추가, 명칭 수정 및 삭제를 할 수 있습니다.
### 카테고리에 따른 게시물 열람 
- 등록된 혹은 등록되지 않은 사용자는, 자신 혹은 타인의 블로그에서 카테고리에 따라 분류된 게시물을 열람할 수 있습니다.

## 태그 관련 기능
### 태그 추가, 명칭 수정 및 삭제
- 등록된 사용자는 자신의 블로그에서 태그 추가, 명칭 수정 및 삭제를 할 수 있습니다.
### 태그에 따른 게시물 열람 
- 등록된 혹은 등록되지 않은 사용자는, 자신 혹은 타인의 블로그에서 태그에 따라 분류된 게시물을 열람할 수 있습니다.

## 댓글 관련 기능
### 댓글 작성, 수정 및 삭제
- 등록된 사용자는 자신 혹은 타인의 블로그에서 댓글 작성, 수정 및 삭제를 할 수 있습니다.

## 알림 메시지 관련 기능
### 알림 메시지 확인 및 삭제
- 등록된 사용자는 자신의 블로그의 게시물에 댓글이 달린 것을 메인 화면에서의 메시지로 확인할 수 있습니다.
- 메시지들은 드롭다운 메뉴의 버튼을 클릭하면 일괄적으로 삭제됩니다.

<br/>

# 아키텍쳐

<br/>

# API 구조

<br/>

# DB 구조

<br/>

# 프론트엔드
## 프론트엔드 개요
이 어플리케이션은 본 프로젝트의 백엔드 어플리케이션과 HTTP 요청과 응답을 통해 상호작용합니다. 이것은 TypeScript와 React를 사용하는 프론트엔드 어플리케이션으로, 브라우저 상에서 페이지를 표시하여 사용자들로부터 입력을 받고 그에 따른 처리를 하며, 이 과정에서 요구되는 리소스 등의 필요 사항을 백엔드에게 전달하여 그로부터 받은 응답을 사용해서 자신의 기능을 수행합니다.

## 프론트엔드 기술 스택
- **언어: TypeScript
- **라이브러리: React

<br/>

# 기능 상세
## 인증과 보안

## 파일 처리

## FE-BE 상호 작용

<br/>

# 사용 예시
<details>
<summary>회원가입과 인증</summary>

</details>
<br/>
<details>
<summary>게시물</summary>

</details>
<br/>
<details>
<summary>카테고리와 태그</summary>

</details>
<br/>
<details>
<summary>댓글</summary>

</details>
<br/>
<details>
<summary>알림 메시지</summary>

</details>
<br/>


# 프로젝트 회고

