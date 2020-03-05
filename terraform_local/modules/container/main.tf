

# Start a container
resource "docker_container" "container" {
  name        = var.container_name
  image       = var.image
  working_dir = var.working_dir
  command     = var.command

  attach   = var.attach
  rm       = var.rm
  must_run = var.must_run

  env = var.env

  dynamic "mounts" {
    for_each = var.mounts
    content {
      target = mounts.key
      source = mounts.value
      type   = "bind"
    }
  }
  dynamic "ports" {
    for_each = var.ports
    content {
      internal = ports.key
      external = ports.value
    }
  }
  dynamic "networks_advanced" {
    for_each = var.network
    content {
      name = networks_advanced.value
    }
  }

}

output "exit_code" {
  value = docker_container.container.exit_code
}
