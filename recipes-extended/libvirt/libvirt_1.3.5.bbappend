pkg_postinst_libvirt_prepend() {
        readlink -f /sbin/init | grep systemd && exit 0
}

do_install_append(){
        # Manually set permissions and ownership to match polkit recipe
        if ${@bb.utils.contains('PACKAGECONFIG', 'polkit', 'true', 'false', d)}; then
                install -d -m 0755 ${D}/${datadir}/polkit-1/rules.d
        fi
}
