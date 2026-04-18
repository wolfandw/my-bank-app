package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Обработка запроса на изменение наличных"

    request {
        method 'POST'
        url('/api/cash') {
            queryParameters {
                parameter 'login': 'user'
                parameter 'value': '10'
                parameter 'action': 'PUT'
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
