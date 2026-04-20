package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Обработка запроса на получение данных счета"

    request {
        method 'GET'
        url('/api/account')
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
                id : "550e8400-e29b-41d4-a716-446655440000",
                user : [
                    id : "550e8400-e29b-41d4-a716-446655440000",
                    login : "user",
                    name : "User",
                    birthdate : '1999-01-01'
                ],
                balance : "10",
                users: [
                        [
                            id : "550e8400-e29b-41d4-a716-446655440001",
                            login : "admin",
                            name : "Admin",
                            birthdate : '1999-01-01'
                        ]
                ]
        )

    }
}
