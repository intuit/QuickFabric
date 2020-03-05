FROM nginx

RUN rm /etc/nginx/conf.d/default.conf

RUN mkdir /etc/nginx/docroot

RUN mkdir /etc/nginx/logs

COPY nginx.conf /etc/nginx/

COPY  default.conf /etc/nginx/conf.d/

COPY build /etc/nginx/docroot/build

RUN chmod 777 -R /etc/nginx/docroot

RUN chmod 777 -R /etc/nginx/logs 
