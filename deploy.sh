mvn package spring-boot:repackage &&
docker build -f Dockerfile -t obt-bot-img . &&
docker stop obt-bot || true &&
docker rm obt-bot || true &&
docker run --name obt-bot -d obt-bot-img