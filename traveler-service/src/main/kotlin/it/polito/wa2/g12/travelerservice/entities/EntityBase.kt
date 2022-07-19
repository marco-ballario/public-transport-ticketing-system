package it.polito.wa2.g12.travelerservice.entities

import org.springframework.data.util.ProxyUtils
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.MappedSuperclass

@MappedSuperclass
abstract class EntityBase<T : java.io.Serializable> {
    companion object {
        private const val serialVersionUID = -43869754L
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private var id: T? = null

    fun getId(): T? = id

    override fun toString(): String {
        return "@Entity ${this.javaClass.name}(id=$id)"
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other === this) return true
        if (javaClass != ProxyUtils.getUserClass(other))
            return false
        other as EntityBase<*>
        return if (null == id) false
        else this.id == other.id
    }

    override fun hashCode(): Int {
        return 31
    }

    fun setId(newId: T) {
        id = newId
    }
}