HOMEPAGE = "http://www.docker.com"
SUMMARY = "Linux container runtime"
DESCRIPTION = "Linux container runtime \
 Docker complements kernel namespacing with a high-level API which \
 operates at the process level. It runs unix processes with strong \
 guarantees of isolation and repeatability across servers. \
 . \
 Docker is a great building block for automating distributed systems: \
 large-scale web deployments, database clusters, continuous deployment \
 systems, private PaaS, service-oriented architectures, etc. \
 . \
 This package contains the daemon and client. Using docker.io is \
 officially supported on x86_64 and arm (32-bit) hosts. \
 Other architectures are considered experimental. \
 . \
 Also, note that kernel version 3.10 or above is required for proper \
 operation of the daemon process, and that any lower versions may have \
 subtle and/or glaring issues. \
 "

SRCREV = "437e0d6f3c9526e2e3d6c94e60c80181a9f71f99"
SRC_URI = "\
	git://github.com/resin-os/balena.git;branch=17.06-resin \
        file://docker.service \
	"

# Apache-2.0 for docker
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://src/import/LICENSE;md5=9740d093a080530b5c5c6573df9af45a"

GO_IMPORT = "import"

S = "${WORKDIR}/git"

DOCKER_VERSION = "17.06.0"
PV = "${DOCKER_VERSION}+git${SRCREV}"

DEPENDS = " \
    go-cli \
    go-pty \
    go-context \
    go-mux \
    go-patricia \
    go-logrus \
    go-fsnotify \
    go-dbus \
    go-capability \
    go-systemd \
    btrfs-tools \
    sqlite3 \
    go-distribution \
    compose-file \
    go-connections \
    notary \
    grpc-go \
    "

DEPENDS_append_class-target = " lvm2"
RDEPENDS_${PN} = "curl aufs-util git util-linux iptables \
                  ${@bb.utils.contains('DISTRO_FEATURES','systemd','','cgroup-lite',d)} \
                 "
RRECOMMENDS_${PN} = "kernel-module-dm-thin-pool kernel-module-nf-nat"

inherit go
inherit goarch
inherit pkgconfig
do_configure[noexec] = "1"

inherit systemd 

SYSTEMD_PACKAGES = "${@bb.utils.contains('DISTRO_FEATURES','systemd','${PN}','',d)}"
SYSTEMD_SERVICE_${PN} = "${@bb.utils.contains('DISTRO_FEATURES','systemd','docker.service docker.socket','',d)}"
SYSTEMD_AUTO_ENABLE_${PN} = "enable"

inherit useradd
USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM_${PN} = "-r docker"

DOCKER_PKG="github.com/docker/docker"

do_compile() {
        # Set GOPATH. See 'PACKAGERS.md'. Don't rely on
        # docker to download its dependencies but rather
        # use dependencies packaged independently.
        cd ${S}/src/import
        rm -rf .gopath

        export GOPATH="${S}/src/import/.gopath:${S}/src/import/vendor:${STAGING_DIR_TARGET}/${prefix}/local/go"
        export GOROOT="${STAGING_DIR_NATIVE}/${nonarch_libdir}/${HOST_SYS}/go"

        # Pass the needed cflags/ldflags so that cgo
        # can find the needed headers files and libraries
        export GOARCH=${TARGET_GOARCH}
        export CGO_ENABLED="1"
        export CGO_CFLAGS="${CFLAGS} --sysroot=${STAGING_DIR_TARGET}"
        export CGO_LDFLAGS="${LDFLAGS} --sysroot=${STAGING_DIR_TARGET}"
        # in order to exclude devicemapper and btrfs - https://github.com/docker/docker/issues/14056
        export DOCKER_BUILDTAGS='exclude_graphdriver_btrfs exclude_graphdriver_devicemapper'

        ./build.sh

}


do_install() {
        install -d ${D}/${sbindir}
        install ${S}/src/import/balena/balena ${D}/${sbindir}/
        cd ${D}/${sbindir}/
        ln -sf balena balena-containerd
        ln -sf balena balena-containerd-ctr
        ln -sf balena balena-containerd-shim
        ln -sf balena balenad
        ln -sf balena balena-proxy
        ln -sf balena balena-runc

        if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
            install -d ${D}${systemd_unitdir}/system
            install -m 644 ${S}/src/import/contrib/init/systemd/docker.* ${D}/${systemd_unitdir}/system
            # replaces one copied from above with one that uses the local registry for a mirror
            install -m 644 ${WORKDIR}/docker.service ${D}/${systemd_unitdir}/system
        fi
}


FILES_${PN} += "${systemd_unitdir}/system/*"

INSANE_SKIP_${PN} += "already-stripped"
