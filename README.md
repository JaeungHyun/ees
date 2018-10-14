# ees


서버는 nginx 사용
나머지 기반은 django

서버와 Django는 아래와 같이 동작한다
the web client <-> the web server <-> the socket <-> uWSGI <-> Python(Django)
