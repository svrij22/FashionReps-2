# To run build docker via Intellij

ssh root@157.90.127.31
docker run -p 8075:8075 fashion-reps:latest

CREATE DATABASE yourdbname;
CREATE USER youruser WITH ENCRYPTED PASSWORD 'yourpass';
GRANT ALL PRIVILEGES ON DATABASE yourdbname TO youruser;

sudo ufw allow from any to any port 8075 proto tcp

#Auto startup script