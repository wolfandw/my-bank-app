INSERT INTO users(id, login, name, birth_date)
VALUES
    ('550e8400-e29b-41d4-a716-446655440000', 'user', 'User', '1999/01/01'),
    ('550e8400-e29b-41d4-a716-446655440001','admin', 'Admin', '1999/01/01');

INSERT INTO accounts(id, user_id, balance)
VALUES
    ('650e8400-e29b-41d4-a716-446655440000', '550e8400-e29b-41d4-a716-446655440000', '100.01'),
    ('650e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440001', '200.02');