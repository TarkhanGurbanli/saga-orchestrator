# Orchestration-based Saga Pattern Implementation

Full enterprise Orchestration Saga pattern implementasiyası Spring Boot, Kafka, PostgreSQL ilə.

## 🏗️ Architecture

```
┌──────────────────────────────────────────────────────────────┐
│                    Saga Orchestrator                         │
│                   (Central Coordinator)                      │
│                                                              │
│  ┌────────────┐  ┌─────────────┐  ┌──────────────┐           │
│  │ Saga State │  │ Step Manager│  │ Compensation │           │
│  │  Machine   │  │             │  │   Handler    │           │
│  └────────────┘  └─────────────┘  └──────────────┘           │
└───────┬──────────────────┬──────────────────┬────────────────┘
        │                  │                  │
        │ Commands         │ Commands         │ Commands
        ▼                  ▼                  ▼
┌───────────────┐  ┌───────────────┐  ┌───────────────┐
│Order Service  │  │Payment Service│  │Inventory Svc  │
│  (Executes)   │  │  (Executes)   │  │  (Executes)   │
└───────┬───────┘  └───────┬───────┘  └───────┬───────┘
        │                  │                  │
        │ Events           │ Events           │ Events
└──────────────────────────┴──────────────────┘────────
                            │
                    Back to Orchestrator
```

## 🎯 Key Differences from Choreography

| Aspect | Choreography | **Orchestration** |
|--------|--------------|-------------------|
| Coordination | Decentralized | **Centralized** |
| Services | Smart (know workflow) | **Dumb (execute commands)** |
| Saga Logic | Distributed | **Single place** |
| Compensation | Each decides | **Orchestrator decides** |
| Visibility | Distributed logs | **Single saga state** |
| Complexity | Service level | **Orchestrator level** |

## 📊 Saga Flow

### Happy Path

```
1. Client → POST /api/sagas
   ↓
2. Orchestrator: Create Saga Instance (STARTED)
   ↓
3. Orchestrator → Command: CREATE_ORDER
   ↓
4. Order Service: Execute & Publish OrderCreatedEvent
   ↓
5. Orchestrator: Update Saga (ORDER_CREATED)
   ↓
6. Orchestrator → Command: PROCESS_PAYMENT
   ↓
7. Payment Service: Execute & Publish PaymentProcessedEvent
   ↓
8. Orchestrator: Update Saga (PAYMENT_COMPLETED)
   ↓
9. Orchestrator → Command: RESERVE_INVENTORY
   ↓
10. Inventory Service: Execute & Publish InventoryReservedEvent
    ↓
11. Orchestrator: Update Saga (COMPLETED) ✅
```

### Failure & Compensation

```
Payment Fails at Step 7:
   ↓
Orchestrator detects PaymentFailedEvent
   ↓
Orchestrator → Command: CANCEL_ORDER
   ↓
Order Service: Cancel & Publish OrderCancelledEvent
   ↓
Orchestrator: Mark Saga as FAILED ❌
```

```
Inventory Fails at Step 10:
   ↓
Orchestrator detects InventoryFailedEvent
   ↓
Orchestrator → Command: REFUND_PAYMENT
   ↓
Payment Service: Refund & Publish PaymentRefundedEvent
   ↓
Orchestrator → Command: CANCEL_ORDER
   ↓
Order Service: Cancel & Publish OrderCancelledEvent
   ↓
Orchestrator: Mark Saga as FAILED ❌
```

## 🚀 Quick Start

### 1. Start Infrastructure

```bash
docker-compose up -d
```

### 2. Start Services (5 terminals)

```bash
# Terminal 1 - Saga Orchestrator (MUST START FIRST)
cd saga-orchestrator && ./gradlew bootRun

# Terminal 2 - Order Service
cd order-service-orch && ./gradlew bootRun

# Terminal 3 - Payment Service
cd payment-service-orch && ./gradlew bootRun

# Terminal 4 - Inventory Service
cd inventory-service-orch && ./gradlew bootRun

# Terminal 5 (Optional) - Notification Service
cd notification-service-orch && ./gradlew bootRun
```

## 🧪 Testing

### Start a Saga

```bash
curl -X POST http://localhost:8085/api/sagas \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "customer-001",
    "productId": "product-001",
    "quantity": 2,
    "amount": 1999.99
  }'
```

Response:
```json
{
  "sagaId": "uuid-here",
  "customerId": "customer-001",
  "orderId": "order-uuid",
  "productId": "product-001",
  "quantity": 2,
  "amount": 1999.99,
  "status": "COMPLETED",
  "steps": [
    {
      "stepType": "CREATE_ORDER",
      "status": "COMPLETED",
      "stepOrder": 1
    },
    {
      "stepType": "PROCESS_PAYMENT",
      "status": "COMPLETED",
      "stepOrder": 2
    },
    {
      "stepType": "RESERVE_INVENTORY",
      "status": "COMPLETED",
      "stepOrder": 3
    }
  ]
}
```

### Get Saga Status

```bash
curl http://localhost:8085/api/sagas/{sagaId}
```

### Get All Customer Sagas

```bash
curl http://localhost:8085/api/sagas/customer/{customerId}
```

### Get All Sagas

```bash
curl http://localhost:8085/api/sagas
```

## 🎯 Saga States

```java
public enum SagaStatus {
    STARTED,              // Saga initiated
    ORDER_CREATED,        // Order created successfully
    PAYMENT_PENDING,      // Payment processing
    PAYMENT_COMPLETED,    // Payment successful
    PAYMENT_FAILED,       // Payment failed
    INVENTORY_PENDING,    // Inventory reservation in progress
    INVENTORY_RESERVED,   // Inventory reserved
    INVENTORY_FAILED,     // Inventory reservation failed
    COMPENSATING,         // Rolling back
    COMPLETED,            // ✅ Saga completed successfully
    FAILED,               // ❌ Saga failed
    CANCELLED             // Saga cancelled
}
```

## 🔄 Command → Event Mapping

| Command | Success Event | Failure Event |
|---------|--------------|---------------|
| CREATE_ORDER | OrderCreatedEvent | OrderCreationFailedEvent |
| PROCESS_PAYMENT | PaymentProcessedEvent | PaymentFailedEvent |
| RESERVE_INVENTORY | InventoryReservedEvent | InventoryFailedEvent |
| CANCEL_ORDER | OrderCancelledEvent | - |
| REFUND_PAYMENT | PaymentRefundedEvent | - |
| RELEASE_INVENTORY | InventoryReleasedEvent | - |

## 💡 Key Implementation Details

### 1. Saga State Machine

Orchestrator-da mərkəzi state machine var:

```java
@Transactional
public void handlePaymentProcessed(PaymentProcessedEvent event) {
    SagaInstance saga = getSaga(event.sagaId());
    saga.setStatus(SagaStatus.PAYMENT_COMPLETED);
    saga.setPaymentId(event.paymentId());
    
    // Move to next step
    executeReserveInventory(saga);
}
```

### 2. Compensation Logic

Orchestrator failure-da avtomatik compensation başladır:

```java
@Transactional
public void startCompensation(SagaInstance saga, String reason) {
    saga.setStatus(SagaStatus.COMPENSATING);
    
    // Reverse order compensation
    if (saga.getPaymentId() != null) {
        compensatePayment(saga);
    }
    if (saga.getOrderId() != null) {
        compensateOrder(saga);
    }
}
```

### 3. Service Commands

Servicelər yalnız command qəbul edir və event publish edir:

```java
@KafkaListener(topics = "order-commands")
public void handleCommand(String message) {
    CreateOrderCommand cmd = parse(message);
    
    Order order = orderRepository.save(...);
    
    // Publish event back to orchestrator
    publishEvent(new OrderCreatedEvent(...));
}
```

## 📊 Database Schema

### Orchestrator DB

**saga_instances:**
- sagaId (PK)
- customerId
- orderId
- productId, quantity, amount
- status (SagaStatus enum)
- paymentId, reservationId
- failureReason
- timestamps

**saga_steps:**
- id (PK)
- sagaId (FK)
- stepType (SagaStepType enum)
- status (SagaStepStatus enum)
- stepOrder
- commandId, eventId
- timestamps

## 🎓 Advantages of Orchestration

### ✅ Pros
1. **Centralized Logic**: Bütün saga flow bir yerdə
2. **Easy Monitoring**: Saga state-i bir yerden izlənir
3. **Simple Services**: Servicelər yalnız command execute edir
4. **Clear Compensation**: Rollback logic mərkəzi
5. **Better Visibility**: Saga progress real-time görünür

### ❌ Cons
1. **Single Point of Failure**: Orchestrator down olsa, saga dayanar
2. **Orchestrator Complexity**: Bütün logic bir yerdə
3. **Tight Coupling**: Services orchestrator-a depend edir

## 🔍 Monitoring

### Saga Dashboard

```bash
# Get all active sagas
curl http://localhost:8085/api/sagas

# Filter by status (if implemented)
curl http://localhost:8085/api/sagas?status=COMPENSATING
```

### Kafka Topics

- `order-commands` / `order-events`
- `payment-commands` / `payment-events`
- `inventory-commands` / `inventory-events`

## 🚨 Error Handling

### Timeout Management

```java
@Scheduled(fixedRate = 60000)
public void checkSagaTimeouts() {
    List<SagaInstance> stuckSagas = 
        repository.findStuckSagas(LocalDateTime.now().minusMinutes(5));
    
    stuckSagas.forEach(this::handleTimeout);
}
```

### Retry Logic

```java
if (saga.getRetryCount() < MAX_RETRIES) {
    saga.setRetryCount(saga.getRetryCount() + 1);
    retryStep(saga);
} else {
    startCompensation(saga, "Max retries exceeded");
}
```

## 📈 Comparison Table

| Feature | Choreography | Orchestration |
|---------|--------------|---------------|
| **Coupling** | Loose | Tighter |
| **Visibility** | Distributed | Centralized ⭐ |
| **Testability** | Complex | Easier ⭐ |
| **Scalability** | Better ⭐ | Good |
| **Maintenance** | Harder | Easier ⭐ |
| **Failure Handling** | Complex | Simpler ⭐ |

## 🎯 When to Use

**Use Orchestration when:**
- Complex workflows with many steps
- Need centralized monitoring
- Business logic frequently changes
- Team prefers centralized control

**Use Choreography when:**
- Simple workflows
- High autonomy needed
- Services are independently developed
- Event-driven architecture preferred

## 📚 Further Reading

- [Saga Pattern - Microservices.io](https://microservices.io/patterns/data/saga.html)
- [Orchestration vs Choreography](https://temporal.io/blog/to-choreograph-or-orchestrate-your-saga-that-is-the-question)

## 🎉 Nəticə

Bu implementasiya tam production-ready orchestration saga pattern-dir. Hər iki pattern-i (Choreography və Orchestration) müqayisə edib lazım olanı seçə bilərsiniz!

**Orchestration** = Mərkəzi idarəetmə, daha asan monitoring və maintenance! 🎯
