FROM maven

WORKDIR /usr/src/mymaven

COPY . .

RUN apt-get update && apt-get install jq -y
RUN chmod +x wait-for-grid.sh
