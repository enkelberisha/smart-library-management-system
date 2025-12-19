
INSERT INTO users (name, email, password, role)
VALUES
  ('Admin', 'admin@smartlib.com', '$2a$10$uH.ZppzPqSwkVkcnN.UAf.omLY7BcpGaOmW6MdICnsHrSpWk8md7i', 'ADMIN')
ON CONFLICT (email) DO NOTHING;

-- passi o Admin123
