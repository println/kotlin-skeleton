INSERT INTO ACCOUNT_LOGINS  (PASSWORD, USERNAME) VALUES
('$2a$10$g0keX76TJOHj8/VfLORfAeD8p4/lRGNKfDjLjCUcugnJTqRclJlcG', 'admin@admin.com'),
('$2a$10$g0keX76TJOHj8/VfLORfAeD8p4/lRGNKfDjLjCUcugnJTqRclJlcG', 'user@user.com')
;

INSERT INTO ACCOUNTS  (ISSUES, CREATED_AT, LAST_MODIFIED, ID, EMAIL, LOGIN_USERNAME, NAME, ROLE, STATUS) VALUES
('1', '2023-09-11 11:24:27.090655', '2023-09-11 11:24:27.128863', 'ef1b4cf8-3e3d-4b99-a11b-82f93b51db91', 'admin@admin.com', 'admin@admin.com', 'admin admin', 'ADMIN', 'ACTIVE'),
('1', '2023-09-11 11:24:27.090655', '2023-09-11 11:24:27.128863', 'a604d14c-50b5-11ee-be56-0242ac120002', 'user@user.com', 'user@user.com', 'user user', 'USER', 'ACTIVE');

INSERT INTO USERS  (CREATED_AT, LAST_MODIFIED, ACCOUNT_ID, ID, FIRST_NAME, LAST_NAME) VALUES
('2023-09-11 11:24:27.090655', '2023-09-11 11:24:27.128863', 'ef1b4cf8-3e3d-4b99-a11b-82f93b51db91', 'ab4e2048-2a2d-4c4e-9c08-32dcc01320dc', 'admin', 'admin'),
('2023-09-11 11:24:27.090655', '2023-09-11 11:24:27.128863', 'a604d14c-50b5-11ee-be56-0242ac120002', '29ed158f-49b8-476e-bf34-118b0ae034b1', 'user', 'user');