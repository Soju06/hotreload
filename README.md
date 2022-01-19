# ⚡HotRelaod
Minecraft Plugin DevTools | Auto Reload Plugin
마인크래프트 플러그인 개발 도구 | 자동 리로드 플러그인

## #️⃣ Commands

 - hotreload
	 -  list - 로드된 플러그인 리스트를 표시합니다.
	 - enable  - 플러그인을 활성화 합니다.
	 - disable - 플러그인을 비활성화 합니다.
	 - load - 플러그인을 로드합니다.
		 - plugin - 플러그인 폴더 안의 플러그인 파일을 선택합니다.
		 - path - 해당 경로의 플러그인 파일을 선택합니다.
	 - unload - 플러그인을 언로드 합니다.
	 - reload - 플러그인을 리로드 합니다.
	 - watch - 플러그인 파일의 변경 사항을 감지합니다.
		 - list - 플러그인 리스트를 표시합니다.
		 - add - 플러그인을 리스트에 추가합니다.
			 - plugin - 플러그인 폴더 안의 플러그인 파일을 선택합니다.
			 - path - 해당 경로의 플러그인 파일을 선택합니다.
		 - remove - 리스트에서 플러그인을 제거합니다.
	- setting - HotReload 설정
		- cooldown - 파일 변경시 리로드 쿨다운 시간을 변경합니다.

## ❗ Precautions
해당 플러그인은 비공유 개발 용도로 개발되어, 해킹 취약점이 발생할 수 있으므로 신뢰할 수 있는 영역에서만 사용하십시오.
의도치 않은 엑세스가 발생할 수 있습니다.

## ⬇️ Download!

Download latest version [here](https://github.com/Soju06/hotreload/releases/latest)

## 🔥 Hot Reload
해당 플러그인의 핵심 기능입니다.
지정한 플러그인의 변경 사항이 생기면 리로드 합니다.

### Usage
1. 개발중인 플러그인 빌드 파일을 리스트에 추가합니다.
![image](https://user-images.githubusercontent.com/34199905/149270212-f6796bac-d6db-4c91-b2e3-5f68cb15b43a.png)
![image](https://user-images.githubusercontent.com/34199905/149272212-031f1aae-e7d3-45ab-9d4b-b1e44f99daca.png)
2. 이제부터 빌드하게 되면 자동으로 리로드됩니다.
![image](https://user-images.githubusercontent.com/34199905/149274324-589330fc-f903-4491-ab2f-bcf8044dc541.png)

## ✔️ Problem solving

### file have been changed가 출력되었지만  늦게 리로드, 또는 리로드되지 않습니다.
과부화 방지를 위해 쿨다운 시스템이 있습니다. 약 1초 동안 파일 수정이 없으면 리로드됩니다.

### 버그를 발견한 경우 / 아이디어 요청
github issues 페이지에 제보하여주십시오.

©️ Soju06 - MIT Licence
