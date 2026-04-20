INSERT INTO outbox(user_id, message, sent)
VALUES
('550e8400-e29b-41d4-a716-446655440000', 'test message 0', false),
('550e8400-e29b-41d4-a716-446655440001', 'test message 1', false),
('550e8400-e29b-41d4-a716-446655440002', 'test message 2', false),
('550e8400-e29b-41d4-a716-446655440003', 'test message 3', true),
('550e8400-e29b-41d4-a716-446655440004', 'test message 4', true),
('550e8400-e29b-41d4-a716-446655440005', 'test message 5', true);

INSERT INTO users(id, login, name, birth_date)
VALUES
    ('550e8400-e29b-41d4-a716-446655440000', 'user', 'User', '1999/01/01'),
    ('550e8400-e29b-41d4-a716-446655440001','admin', 'Admin', '1999/01/01');

INSERT INTO accounts(id, user_id, balance)
VALUES
    ('650e8400-e29b-41d4-a716-446655440000', '550e8400-e29b-41d4-a716-446655440000', '100.01'),
    ('650e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440001', '200.02');