FROM bellsoft/liberica-openjdk-centos:14.0.2-13
COPY ./target/obt-counter-1.0.jar /home/obt-counter-1.0.jar
CMD ["java","-jar","/home/obt-counter-1.0.jar"]