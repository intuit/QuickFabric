CREATE USER 'qf_admin'@'%' IDENTIFIED BY 'supersecret';
GRANT ALL PRIVILEGES ON * . * TO 'qf_admin'@'%';
FLUSH PRIVILEGES;