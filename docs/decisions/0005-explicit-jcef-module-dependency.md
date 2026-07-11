# ADR-0005: JCEF는 명시적인 플러그인 모듈 의존성이다

- **상태:** 수락됨
- **날짜:** 2026-07-11

## 맥락

MarkdownNeat은 렌더러를 열기 전에 `JBCefBrowser` 인스턴스를 만들고 `JBCefApp`을 확인한다.

IntelliJ IDEA 2026.2의 Marketplace 설치 테스트에서 플러그인 클래스 로더가 `JBCefApp`을 해석하지 못했다.

플러그인은 일반 플랫폼 모듈만 선언하고 있었다.

이 선언만으로는 지원하는 모든 IDE 빌드에서 JCEF 라이브러리 모듈이 플러그인 클래스 로더에 보이지 않는다.

## 결정

MarkdownNeat은 `com.intellij.modules.platform` 외에 `plugin.xml`에서 별도 descriptor를 갖는 선택적 `intellij.libraries.jcef` 의존성을 선언해야 한다.

JCEF를 런타임에 사용할 수 없는 IDE를 위해 플러그인은 일반 텍스트 fallback을 계속 유지해야 한다.

명시적 의존성은 JCEF API를 링크할 수 있는지 제어하고, 런타임 확인은 네이티브 브라우저를 사용할 수 있는지 제어한다.

## 결과

- 최신 JCEF 지원 제품은 필요한 API를 MarkdownNeat 클래스 로더에 노출한다.
- 플랫폼을 통해 JCEF를 노출하지만 해당 모듈을 선언하지 않는 이전 제품도 계속 지원한다.
- 회귀 테스트가 매 릴리스 전에 필요한 모듈 선언과 보조 descriptor를 확인한다.
