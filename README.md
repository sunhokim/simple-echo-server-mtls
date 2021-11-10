# simple-echo-server-mtls
simple echo server - mtls

## create self signed root certificate

### openssl config file
```
vi ca_default_root.cnf

[ req ]
default_bits            = 2048
default_md              = sha1
default_keyfile         = rootca.com.key
distinguished_name      = req_distinguished_name
req_extensions          = v3_ca
x509_extensions         = v3_ca


[ v3_ca ]
basicConstraints = critical, CA:TRUE
keyUsage = digitalSignature, keyCertSign, cRLSign
subjectKeyIdentifier   = hash
#authorityKeyIdentifier = keyid:always,issuer:always


[req_distinguished_name ]
countryName                     = Country Name (2 letter code)
countryName_default             = KR
countryName_min                 = 2
countryName_max                 = 2

# 회사명 입력
organizationName              = Organization Name (eg, company)
organizationName_default      = rootca.com
 
# 부서 입력
organizationalUnitName          = Organizational Unit Name (eg, section)
organizationalUnitName_default  = rootca.com
 
# SSL 서비스할 domain 명 입력
commonName                      = Common Name (eg, your name or your server's hostname)
commonName_default             = rootca.com
commonName_max                  = 64 
```


```
vi user_service_wildcard.cnf

[ req ]
default_bits            = 2048
default_md              = sha1
default_keyfile         = rootca.com.key
distinguished_name      = req_distinguished_name
extensions             = v3_user

[ v3_user ]
# Extensions to add to a certificate request
basicConstraints = CA:FALSE
authorityKeyIdentifier = keyid,issuer
subjectKeyIdentifier = hash
keyUsage = nonRepudiation, digitalSignature, keyEncipherment
extendedKeyUsage = serverAuth,clientAuth
subjectAltName          = @alt_names
[ alt_names]
DNS.1   = *.service.com

[req_distinguished_name ]
countryName                     = Country Name (2 letter code)
countryName_default             = KR
countryName_min                 = 2
countryName_max                 = 2

# 회사명 입력
organizationName              = Organization Name (eg, company)
organizationName_default      = service.com

# 부서 입력
organizationalUnitName          = Organizational Unit Name (eg, section)
organizationalUnitName_default  = api.service.com

# SSL 서비스할 domain 명 입력
commonName                      = Common Name (eg, your name or your server's hostname)
commonName_default             = api.service.com
commonName_max                  = 64
```


### create root certificate
```
# create private key
openssl genrsa -out rootca.com.key 2048

# create certficate request file
openssl req -new \
-config ca_default_root.cnf \
-extensions v3_ca \
-subj '/C=KR/ST=Seoul/O=Org/OU=OrgUnit/CN=rootca.com/' \
-key rootca.com.key \
-out rootca.com.csr

# self sign
openssl x509 -req -days 3650 \
-extfile ca_default_root.cnf \
-extensions v3_ca \
-signkey rootca.com.key \
-in rootca.com.csr \
-out rootca.com.crt

# check certificate
openssl x509 -text -in rootca.com.crt
```

### create leaf certificate
```
# create private key
openssl genrsa -out api1.service.com.key 2048

# create certficate request file
openssl req -new \
-config user_service_wildcard.cnf \
-extensions v3_user \
-subj '/C=KR/ST=Seoul/O=Org/OU=OrgUnit/CN=api1.service.com/serialNumber=201-22-00001/' \
-key api1.service.com.key \
-out api1.service.com.csr

# certificate signing
openssl x509 -req -days 365 \
-extfile user_service_wildcard.cnf \
-extensions v3_user -in api1.service.com.csr \
-CA rootca.com.crt -CAcreateserial \
-CAkey rootca.com.key \
-out api1.service.com.crt


openssl x509 -text -in api1.service.com.crt
```


### Convert pem to jks
```
# 서버에서 사용할 ssl 인증서 생성 및 jks 변환.
cat api1.service.com.key api1.service.com.crt rootca.com.crt > identity.pem

openssl pkcs12 -export -out identity.p12 -in identity.pem

keytool -importkeystore -srckeystore identity.p12 -srcstoretype pkcs12 -destkeystore identity.jks -deststoretype jks

keytool -list -keystore identity.jks

# 서버에서 사용할 trust-store 생성.
keytool -v -importcert -file rootca.com.crt -alias client -keystore trust-store.jks -noprompt
```
|옵션|설명|
|---|---|
|test|aaaa|
