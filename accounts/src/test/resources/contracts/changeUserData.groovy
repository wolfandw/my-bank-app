package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Обработка запроса на изменение данных пользователя"

    request {
        method 'POST'
        url('/api/account') {
            queryParameters {
                parameter 'login': 'user'
                parameter 'name': 'User'
                parameter 'birthdate': '1999-01-01'
            }
        }
        headers {
            contentType('application/json')
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
            contentType('application/json')
        }
        body(
                userId : "550e8400-e29b-41d4-a716-446655440000",
                login : "user",
                accepted : true,
                message: "test message"
        )
    }
}
