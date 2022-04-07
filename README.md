# 🛸 소행성 : 나를 바꾸는 습관 🛸

<img width="400" alt="스크린샷 2022-04-04 오후 8 28 48" src="https://user-images.githubusercontent.com/89513776/161534752-987d41bc-bf24-454f-82c1-acd042e628fc.png">


[소행성 바로가기🚀](https://sohangsung.co.kr/)

#### BACKEND MEMBERS
[문병민👩‍🚀](https://github.com/qudalsrnt3x/)
[이혁준👩‍🚀](https://github.com/galaxian/)
[조혜원👩‍🚀](https://github.com/chw-owo/)
</br></br>

## 1️⃣ 프로젝트 설명⚡️
<pre>“나와 같은 목표로 함께할 누군가가 필요해!”

먼 미래보다 행복한 하루를 바라는 MZ세대 나아가 모든 세대들의 트렌드 ‘갓생살기’

때로는 가까운 지인이 아닌 나를 봐줄 익명의 다수가 필요한 이들을 위해 

함께할 수 있는 팀원들을 편하게 구하고, 동기부여까지 받을 수 있는 플랫폼을 기획/제작하였습니다.</pre>

<p>저희의 프로젝트가 더 궁금하신가요?⤵️</p>
[📍소행성 팀 노션](https://sohaengseong.notion.site/232e061b559f46b3a5f9b38fcfaedb2b/) </br>
[📍소행성 인스타그램](https://www.instagram.com/sohangsung.official/?hl=ko/)

</br></br>

## 2️⃣ 프로젝트 요약🌈

* 기간 :  2022.02.25 ~ 2022.04.08
* 개발 언어 : Java
* 개발 라이브러리 : Spring
* 배포 환경 : AWS
* 협업 툴 : Git / Notion 

<details>
  <summary>DB 테이블</summary>
  ![](https://user-images.githubusercontent.com/94841127/162137926-c65d29ab-09ee-4cec-9e96-8ccc4dec72fa.png)
</details>

</br>

## 3️⃣ 아키텍처✨

<img src="https://apricot-tarsal-29a.notion.site/image/https%3A%2F%2Fs3-us-west-2.amazonaws.com%2Fsecure.notion-static.com%2F59c621b1-4a4b-4e85-8400-ecd509294f7d%2FUntitled.png?table=block&id=fa0c9fa2-8d0b-42a8-b032-c7cda177cdb0&spaceId=758b74dd-be75-47e6-8ad5-a4133966de3f&width=2000&userId=&cache=v2"/>

</br></br>


## 4️⃣ 프로젝트 주요기능🌟
### ⓞ 주요 기능
* 로그인, 회원가입, 소셜로그인 
* 검색
* 기록 : 마이 리포트 
* 소통 : 레벨업/마이 행성(캐릭터)  
* 보상 : 랭킹 / 채팅


### ① 로그인, 회원가입, 소셜로그인
* 회원가입 시 이메일 인증 메일을 통해 이메일 확인
* 카카오톡 api를 이용한 소셜로그인

### ② 검색
> 검색기능을 이용해 많은 카테고리중에서 원하는 챌린지를 찾을 수 있도록 구현

* 검색
  - 제목, 태그, 카테고리에 관한 결과를 가져올때 중복이 발생하지 않도록 JPQL을 사용해 중복을 제거했습니다.
  - 검색어 전후로 공백문자 입력시 제거하도록 처리하여 검색의 정확도를 증가시켰습니다.
  - 가장 많이 사용된 태그를 DB에서 찾아 추천 검색어로 사용하도록 구현했습니다.

* 페이징
  - 많은 챌린지를 한 번에 불러오는 페이지인만큼 pagination 처리 및 다음 페이지 유무를 boolean 값으로 반환하여 무한스크롤을 구현했습니다.
 

### ③ 챌린지

* 챌린지 등록/수정
  - 챌린지를 등록할 때 값을 미입력한 뒤 등록하기를 누르면 토스트 메시지와 스크롤 포커싱이 되도록 구현하여 편의성을 높혔습니다.
  - 이미지를 한 번에 3개씩 등록할 수 있게 for문을 이용해 사용자가 업로드한 이미지 파일을 image 배열안에 넣어주도록 구현하였습니다.

* 챌린지 소개페이지
  - 사용자가 업로드한 이미지를 슬라이드 형식으로 볼 수 있게 구현하였습니다.
  - dayjs를 이용해 챌린지 기간을 자동으로 계산하고, 계산한 기간으로 조건문을 걸어 버튼 예외처리(입장가능,불가능,이미입장..)를 하였습니다.
  - 챌린지 공유를 했을 때는 history가 없는 점을 파악해 history.length가 0이라면 메인으로 이동하게 처리해 사용자의 편의를 높혔습니다.


   
### ④ 멤버 전용 페이지

* 챌린지 인증
  - 실시간 인증 피드
    + 인증을 하면 경험치를 얻고, 실시간 인증 피드에 올라가게 됩니다.
    + 다른 챌린지멤버가 인증한 게시글에 댓글을 달며 소통할 수 있게 구현하였습니다
   
* 위클리 리포트
  - 멤버 페이지 메인에 있는 위클리 리포트로 챌린지 멤버가 얼마나 인증을 했는 지 알 수 있도록 그래프 형식으로 구현하였습니다.

* 실시간 채팅
  - 멤버들간에 실시간 채팅으로 챌린지에 관련된 정보를 공유할 수 있게 sockjs, stomphandler, redis 을 이용해 구현한 기능입니다.


### ⑤ 마이페이지

* 마이페이지
  - 인증한 챌린지에는 스탬프가 찍히도록 구현하였습니다.
  - 나만의 외계인 토비
    + 사용자가 인증을 하면 오르는 경험치를 먹고 자라는 소행성의 외계인 토비, 서버와 통신하여 레벨업을 하면 토비의 모습이 변하게 구현하였습니다.

* 프로필 수정
  - 비밀번호 확인 시 디바운스를 활용하여 서버에 자동으로 비밀번호 확인을 할 수 있어 사용자의 편의성을 높혔습니다.

* 진행 상황 및 개설 유무
  - 자신이 참가한 챌린지의 상태값을 확인해 진행 예정, 진행중, 완료 상태의 챌린지를 구분해서 확인 할 수 있도록 하였습니다.
  - 자신이 완료한 챌린지의 성공 여부 및 기록을 확인 할 수 있도록 구현했습니다.

* 내가 개설한 챌린지 수정/삭제
  - 런칭 이후 유저피드백을 반영해 구현한 기능입니다.
  - 챌린지 멤버페이지로 이동하는 플로우가 복잡해 편의성과 접근성을 높히기 위해 개설탭에서 바로 챌린지를 수정/삭제할 수 있도록 하였습니다.

* 마이리포트
  - 그동안의 사용자의 노력을 잊지 않도록 마이 행성 > 마이리포트에서 지난 챌린지와 성공 여부를 확인할 수 있게 구성하였습니다.
  - 지난 챌린지를 클릭 할 경우 팀원들과 함께 했던 기록들이 고스란히 남아있어 사용자의 동기부여를 유도하였습니다.


### ⑥ 기타

* 닉네임 수정 기능
  -  런칭 이후 유저피드백을 반영해 구현한 기능입니다.
  - 기존에는 닉네임이 수정 불가하였지만, 카카오 사용자들의 피드백을 반영해 닉네임을 수정할 수 있도록 반영하였습니다.
  - 소셜로그인을 했을 경우 디폴트 값으로 소셜매체의 닉네임이 들어옵니다.

* 알림
  - 사용자가 놓친 챌린지를 확인하고, 쌓이는 경험치를 확인할 수 있도록 알림기능을 구현하였습니다.
  - 스케줄러를 사용하여 정해진 시간에 알림이 사용자에게 갈 수 있도록 구현했습니다.

</br>

## 5️⃣ 트러블 슈팅🚀

### DB 성능 개선
<details>
  <summary>데이터 조회 시간 단축</summary>
  
  * 도입 이유 : 플랫폼 특성 상 많은 게시물과 채팅 메시지에 대한 최적화 필요
  * 문제 상황
    - fetchType Lazy 에서 데이터 조회 시 N + 1 문제가 발생
    - 1개의 데이터 호출 시 N개의 쿼리를 추가로 발생
  * 해결 방안 1
    - fetchType EAGER 사용
    - jpa의 find method 사용 시 N+1 문제 해결
    - JPQL 사용시 글로벌 패치 전략을 고려하지 않고 JPQL만 고려해 N+1 문제 발생
    - JPQL을 사용해야 하는 경우가 있으므로 채택하지 않음
  * 해결 방안 2
    - fetch join 사용
    - 지연로딩시에도 프록시가 아닌 실제 엔티티를 가지고 오기 때문에 N+1 문제가 발생하지 않음
    - fetch join 문제
      - pagination 사용시 원하는 결과를 출력하나 경고문 및 limit 쿼리 발생하지 않음
      - limit, offset을 통한 쿼리가 아닌 인메모리에 모든 쿼리를 저장해 application 단에서 페이징처리(Out of memory 위험)
      - 둘 이상의 컬렉션을 fetch join 할 경우 오류 발생
  * 해결 방안 3
    - batchsize 적용
    - where 조건절이 in 절로 변경되어 실행
    - 추가 쿼리는 발생하지만 N번 만큼의 양은 아님
    - paginaiton 시에 limit 쿼리 확인
    - 둘 이상 컬렉션 일반 join 시 사용가능
  * 의견 결정
    - 사용 가능한 경우 우선 fetch join 사용
    - fetch join 사용이 불가한 경우 batchsize를 이용해 DB 성능 개선
  * 결과
  
    |항목|개선 전|개선 후|개선율| 
    |:—|:—|:—|:—|
    |전체 챌린지 조회|47ms|103ms|363.1%| 
    |카테고리 검색|300ms|59ms|408.5%|
    |검색|345ms|97ms|255.7%|
    |인증 게시글 상세 조회|220ms|149ms|47.7%|
    |인증 게시글 전체 조회|261ms|176ms|48.3%|
    |챌린지 상세 조회|196ms|74ms|164.9%|
    |레포트 조회|241ms|241ms|0%|
    |행성 추천|782ms|567ms|37.9%|
    |채팅 메시지 조회|211ms|194ms|8.8%|
    |채팅방 목록 조회|521ms|385ms|35.3%|
    |마이페이지 챌린지 조회|520ms|251ms|107.2%|
  
  </details>

## 6️⃣ 피드백 개선🔥
<details>
  <summary>챌린지 수정/삭제 플로우 개선</summary>
  
  * 피드백
  <pre>내 정보에서 진행예정인 챌린지를 들어가면 행성 나가기가 보입니다.
진행중인 소행성말고 진행예정도 리스트에서 들어갔을 때 취소할 수 있으면 좋겠습니다.</pre>
  * 개선 내용
    - 원하는 기능을 원하는 곳에서 찾을 수 있도록, 마이페이지 > 개설 탭에 수정/삭제 기능 추가
  
  <img width="250" alt="image" src="https://user-images.githubusercontent.com/89513776/161965405-93b858d1-f86c-4478-9055-cbe7002fc433.png">
  
</details>
  
<details>
  <summary>챌린지 등록 페이지 작성 편의성 향상</summary>
  
  * 피드백
  <pre>개설하기 버튼이 비활성화 됐을 때 무엇을 입력 안했는지 알려주면 좋을 것 같습니다.</pre>
  * 개선 내용
    - 미입력 값이 무엇인지 사용자가 고민하지 않도록, 토스트 메시지 노출과 미입력 항목으로 스크롤 이동 및 포커싱 적용
  
  <img width="200" alt="image" src="https://user-images.githubusercontent.com/89513776/161965459-24a25ecc-e18a-4793-bca7-75ff28ff4d2e.png">
  
</details>
  
<details>
  <summary>서비스에 대한 이해 부족으로 이탈률이 높아지는 것에 대한 이슈 개선</summary>
  
  * 피드백
  <pre>전반적인 서비스에 대한 안내 및 설명이 부족합니다.</pre>
  * 개선 내용
    - 소행성을 사용자가 더 잘 사용할 수 있도록 사용자 가이드 추가
    - 메인에 후킹 요소로 배치하여 클릭 유도
  
  <img width="200" alt="image" src="https://user-images.githubusercontent.com/89513776/161965719-d8a1ae87-773f-4ee5-b702-1f432b803b1c.png">
  
  
</details>


## 7️⃣ 사이트 데모🎥

<details>
<summary>데모영상</summary>
  
|메인페이지|오늘의행성(무한스크롤 적용)|사용자가이드| 
|:---:|:---:|:---:| 
|<img src="https://user-images.githubusercontent.com/89513776/161700363-6103dccc-5145-4905-915e-6ceabeea6c24.gif" width="200"/>|<img src="https://user-images.githubusercontent.com/89513776/161700723-a692fc85-aa15-44df-abbe-456d6b26c00b.gif" width="200"/>|<img src="https://user-images.githubusercontent.com/89513776/161701259-8665226f-0ad8-4ac6-bbcf-ee2e547b6951.gif" width="200"/>|
|검색|카테고리|알림|
|<img src="https://user-images.githubusercontent.com/89513776/161700674-a010391b-a6a0-4407-838d-00125f8ac053.gif" width="200"/>|<img src="https://user-images.githubusercontent.com/89513776/161700062-bea2a953-fb27-4c6b-b7d3-36c6fd5d3bfc.gif" width="200"/>|<img src="https://user-images.githubusercontent.com/89513776/161701136-79f1089c-f483-4af1-bb98-be52601a8c09.gif" width="200"/>|
|챌린지 소개|공유기능|챌린지 등록|
|<img src="https://user-images.githubusercontent.com/89513776/161701623-a04a32ba-a091-4e2b-87da-ab8dc2ed26aa.gif" width="200"/>|<img src="https://user-images.githubusercontent.com/89513776/161701647-d5bfd171-0acd-4964-8d01-ca861eac3292.gif" width="200"/>|<img src="https://user-images.githubusercontent.com/89513776/161701808-de3b1a05-0ba5-40d0-a9b2-73e5e6ad4521.gif" width="200"/>|
|마이페이지|챌린지 수정|프로필수정|
|<img src="https://user-images.githubusercontent.com/89513776/161702057-f8f0ffb1-2e0a-4d67-9b9a-51e5335c1a6c.gif" width="200"/>|<img src="https://user-images.githubusercontent.com/89513776/161702023-c3340c26-4730-4203-b033-09c686c5614f.gif" width="200"/>|<img src="https://user-images.githubusercontent.com/89513776/161702556-198e8c61-9660-4375-b16b-17aaa6b72a1b.gif" width="200"/>|
|위클리리포트|인증 피드(+댓글)|실시간 채팅(무한스크롤 적용)|
|<img src="https://user-images.githubusercontent.com/89513776/161702625-0e6382c4-43b1-4984-863b-6d09f06da9e7.gif" width="200"/>|<img src="https://user-images.githubusercontent.com/94282246/161708962-fb9a2949-159e-4abd-a120-5985ef76d01e.gif" width="200"/>|<img src="https://user-images.githubusercontent.com/89513776/161702194-e2c8fa0a-67d6-42e5-9fc7-407353e53be9.gif" width="200"/>|

</details>

## 8️⃣ 사용한 라이브러리(패키지)

|라이브러리명|내용|참고| 
|:—|:—|:—| 
|security|로그인 인증 및 권한|로그인 및 jwt 적용|
|jpa|ORM 표준|데이터베이스와 연결|
|lombok|어노테이션 기반 코드 자동 완성||
|validation|유효성 체크||
|mail|메일 전송|구글 메일 전송|
|thymeleaf|템플릿 엔진|인증 메일 html에 사용|
|jwt|json web token|로그인 유저 확인 시 사용|
|devtools|개발 편의 제공||
|redis|인메모리 데이터 저장소|외부 message handler로 사용|
|websocket|소켓 통신|소켓 통신|
|sentry|에러 로그 저장|에러 로그 모니터링|
