package skeleton.app.domain.user.web

import skeleton.app.domain.user.User
import skeleton.app.domain.user.UserFilter
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import skeleton.app.configuration.constants.Endpoints
import java.net.URI
import java.util.*

@RequestMapping(Endpoints.USER, Endpoints.USER_)
@RestController
class UserController(val service: UserWebService) {
    @GetMapping("test")
    fun checkEvents() {
        //service.sendCreateOrderCommand(UUID.randomUUID(), "pickupAddress", "deliveryAddress")
    }

    @GetMapping
    fun getAll(
            @RequestParam(required = false) coordinates: String?,
            pageable: Pageable): Page<User> {
        val filter = UserFilter()
        return service.findAll(filter, pageable)
    }

    @GetMapping("/{id}")
    fun getById(
            @PathVariable("id") id: UUID): User {
        return service.findById(id)
    }

    @PostMapping
    fun createOrder(
            @RequestBody data: UserDto): ResponseEntity<User> {
        val entity = service.createOrder(data.customerId, data.pickupAddress, data.deliveryAddress)

        val location: URI = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(entity.id).toUri()

        return ResponseEntity.created(location).body(entity)
    }

    @PutMapping("/{id}/approve")
    fun tempApproveOrder(
            @PathVariable("id") id: UUID,
            @RequestBody data: PaymentDto): User {
        return service.approvePayment(id, data.value)
    }

    @PutMapping("/{id}/refuse")
    fun tempRefuseOrder(
            @PathVariable("id") id: UUID): User {
        return service.refusePayment(id, "cant pay")
    }
}