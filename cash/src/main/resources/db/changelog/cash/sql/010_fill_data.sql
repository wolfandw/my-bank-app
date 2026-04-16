INSERT INTO outbox(user_id, message, sent)
VALUES
('550e8400-e29b-41d4-a716-446655440000', 'test message 0', false),
('550e8400-e29b-41d4-a716-446655440001', 'test message 1', false),
('550e8400-e29b-41d4-a716-446655440002', 'test message 2', false),
('550e8400-e29b-41d4-a716-446655440003', 'test message 3', true),
('550e8400-e29b-41d4-a716-446655440004', 'test message 4', true),
('550e8400-e29b-41d4-a716-446655440005', 'test message 5', true);