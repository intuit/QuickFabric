provider "docker" {
  alias = "docker"
  host  = "unix:///var/run/docker.sock"
}
