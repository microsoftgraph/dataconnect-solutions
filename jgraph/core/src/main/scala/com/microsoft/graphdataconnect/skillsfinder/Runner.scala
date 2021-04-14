package com.microsoft.graphdataconnect.skillsfinder

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration

@SpringBootApplication(exclude = Array(classOf[SecurityAutoConfiguration]))
class Runner {

}

object Runner extends App {
  SpringApplication.run(classOf[Runner])
}
