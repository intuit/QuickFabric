
locals {

  local_app_path     = abspath("../")
  container_app_path = "/opt/quickfabric"
}

resource "null_resource" "tmp_dir" {
  provisioner "local-exec" {
    command = "mkdir -p ${var.data_store_path}/mysql"
  }
}

# Find the latest Ubuntu precise image.
resource "docker_image" "maven_image" {
  name         = "maven"
  keep_locally = true
}

# Find the latest Ubuntu precise image.
resource "docker_image" "node_image" {
  name         = "node"
  keep_locally = true
}
resource "docker_network" "quickfabric_network" {
  name = "quickfabric"
}

module "middleware" {
  source = "./modules/container"

  container_name = "maven"
  command        = ["mvn", "clean", "install", "-DskipTests", "-Dmaven.repo.local=/opt/data/.m2"]
  working_dir    = "/opt/quickfabric/Middleware/"
  data_store     = var.data_store_path

  image  = "${docker_image.maven_image.latest}"
  attach = true

  mounts = {
    "${local.container_app_path}" : "${local.local_app_path}",
    "/opt/data/" : "${var.data_store_path}"
  }
}

module "frontend" {
  source = "./modules/container"

  container_name = "node"
  command        = ["bash", "-x", "./build.sh"]
  working_dir    = "/opt/quickfabric/Frontend"
  data_store     = var.data_store_path

  image  = "${docker_image.node_image.latest}"
  attach = true

  mounts = {
    "${local.container_app_path}" : "${local.local_app_path}",
    "/opt/data/" : "${var.data_store_path}"
  }

}


resource "null_resource" "DB_container" {
  provisioner "local-exec" {
    command     = "docker build -t db ." #
    working_dir = "${local.local_app_path}/DB/"
  }
  provisioner "local-exec" {
    when    = "destroy"
    command = "docker rmi -f $(docker images -f reference=db -q)"
  }

}

module "db" {
  source = "./modules/container"

  container_name = "db"
  data_store     = var.data_store_path

  image = "db"

  network = { "name" : "${docker_network.quickfabric_network.name}" }
  ports   = { "3306" : "3306" }

  mounts = {
    "${local.container_app_path}" : "${local.local_app_path}",
    "/opt/data/" : "${var.data_store_path}"
    "/var/lib/mysql/" : "${var.data_store_path}/mysql"
  }
  env = [
    "MYSQL_ROOT_PASSWORD=${var.MYSQL_PASSWORD}",
    "depends_on = ${null_resource.DB_container.id}"
  ]

}

resource "null_resource" "EMR_container" {
  provisioner "local-exec" {
    command     = "docker build -t emr ."
    working_dir = "${local.local_app_path}/Middleware/emr/"
  }
  provisioner "local-exec" {
    when    = "destroy"
    command = "docker rmi -f $(docker images -f reference=emr -q)"
  }
}

module "emr" {
  source = "./modules/container"

  container_name = "emr"
  data_store     = var.data_store_path

  image = "emr"

  network = { "name" : "${docker_network.quickfabric_network.name}" }
  ports   = { "8080" : "8080" }

  env = [
    "MYSQL_PASSWORD=${var.MYSQL_PASSWORD}",
    "AES_SECRET_KEY=${var.AES_SECRET_KEY}",
    "depends_on = ${null_resource.EMR_container.id}"
  ]

}

resource "null_resource" "SCHEDULER_container" {
  provisioner "local-exec" {
    command     = "docker build -t scheduler ."
    working_dir = "${local.local_app_path}/Middleware/schedulers/"
  }
  provisioner "local-exec" {
    when    = "destroy"
    command = "docker rmi -f $(docker images -f reference=scheduler -q)"
  }
}

module "scheduler" {
  source = "./modules/container"

  container_name = "scheduler"
  data_store     = var.data_store_path

  image = "scheduler"

  network = { "name" : "${docker_network.quickfabric_network.name}" }

  env = [
    "MYSQL_PASSWORD=${var.MYSQL_PASSWORD}",
    "AES_SECRET_KEY=${var.AES_SECRET_KEY}",
    "depends_on = ${null_resource.SCHEDULER_container.id}"
  ]

}


resource "null_resource" "FRONTEND_container" {
  provisioner "local-exec" {
    command     = "docker build -t frontend ."
    working_dir = "${local.local_app_path}/Frontend/"
  }
  provisioner "local-exec" {
    when    = "destroy"
    command = "docker rmi -f $(docker images -f reference=frontend -q)"
  }
}

module "frontend_container" {
  source = "./modules/container"

  container_name = "frontend"
  data_store     = var.data_store_path

  image = "frontend"

  network = { "name" : "${docker_network.quickfabric_network.name}" }
  ports   = { "80" : "80" }

  env = [
    "depends_on = ${null_resource.FRONTEND_container.id}"
  ]

}




