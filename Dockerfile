FROM openjdk:8-stretch

ARG DOCKERIZE_ARCH=amd64
ENV DOCKERIZE_VERSION=v0.6.0
ENV ACESTREAM_VERSION="3.1.16_debian_8.7"

# Install Acestream
RUN apt-get update && apt-get install --no-install-recommends --yes \
		curl dumb-init openvpn dnsutils psmisc \
		libpython2.7 \
		net-tools \
		python-apsw \
		python-lxml \
		python-m2crypto \
		python-pkg-resources \
    && mkdir -p /opt/acestream /var/log/acestream \
    && wget -qO- http://dl.acestream.org/linux/acestream_${ACESTREAM_VERSION}_x86_64.tar.gz | tar xz -C /opt/acestream --strip-components=1 \
	&& echo "/opt/acestream/lib" >>/etc/ld.so.conf \
	&& ln -s /usr/lib/x86_64-linux-gnu/libssl.so.1.0.2 /usr/lib/libssl.so.1.0.0 \
	# Install missing lib from jessie
	&& echo "deb http://httpredir.debian.org/debian jessie main contrib non-free" > /etc/apt/sources.list.d/jessie.list \
    && echo "deb-src http://httpredir.debian.org/debian jessie main contrib non-free" >> /etc/apt/sources.list.d/jessie.list \
    && echo "deb http://security.debian.org/ jessie/updates main contrib non-free" >> /etc/apt/sources.list.d/jessie.list \
    && echo "deb-src http://security.debian.org/ jessie/updates main contrib non-free" >> /etc/apt/sources.list.d/jessie.list \
	&& apt-get update \
	&& apt-get install --no-install-recommends --yes libssl1.0.0 \
	&& /sbin/ldconfig \
	&& apt-get clean \
    && rm --force --recursive /var/lib/apt/lists

# Set up static hosting
RUN mkdir -p /opt/static
WORKDIR /opt/static
RUN wget https://codeload.github.com/andyno/msga-web/zip/master -O static.zip
RUN unzip static.zip

# Set up MSGA manager app
RUN mkdir -p /opt/msga
WORKDIR /opt/acestream-server-manager/
CMD ["dumb-init", "java", "-jar", "/opt/acestream-server-manager/app.jar"]

ADD openvpn /opt/openvpn/
ADD src/main/resources/application-docker.yml /opt/acestream-server-manager/application.yml
ADD build/libs/server-manager-*.jar /opt/acestream-server-manager/app.jar

