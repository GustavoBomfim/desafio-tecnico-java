INSERT INTO roles (description)
VALUES ('ADMIN'),
       ('MANAGER'),
       ('CUSTOMER'),
       ('SUPPORT');

INSERT INTO claims (decription, active)
VALUES ('CAN_CREATE_USERS', true),
       ('CAN_DELETE_USERS', true),
       ('CAN_VIEW_REPORTS', true),
       ('CAN_REFUND_TRANSACTIONS', true);