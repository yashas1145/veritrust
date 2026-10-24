ALTER TABLE credentials
ADD (
    signature VARCHAR2(128 CHAR),
    issuer_public_key VARCHAR2(100 CHAR)
);
