package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Обработка запроса на отправку уведомления"

    request {
        method 'POST'
        url('/api/notifications') {
            queryParameters {
                parameter 'outboxId': '550e8400-e29b-41d4-a716-446655440000'
                parameter 'userId': '550e8400-e29b-41d4-a716-446655440000'
                parameter 'message': 'Тестовое сообщение для отправки'
            }
        }
        headers {
            contentType(applicationJson())
            header 'Authorization', value(
                    // Для консьюмера (WireMock): любой Bearer-токен
                    consumer(regex('Bearer\\s+.+')),
                    // Для провайдера (MockMvc-тест): ровно этот токен
                    producer('Bearer test-token')
            )
        }
    }

    response {
        status 200
        headers {
            contentType(textPlain())
        }
        body("550e8400-e29b-41d4-a716-446655440000")
    }
}
