
FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

# For the zynq boards we need additional kernel configuration
EXTRASRCZYNQ = ""
EXTRASRCZYNQ_xilinx-zynq = " \
            file://xilinx-zynq-extra.scc \
            file://xilinx-zynq-extra.cfg \
          "

SRC_URI += "${EXTRASRCZYNQ}"

KMACHINE_intel-corei7-64 = "${@bb.utils.contains('KERNEL_FEATURES', 'leafhill', 'leafhill', 'computestick', d)}"
KBRANCH_intel-corei7-64 = "${@bb.utils.contains('KERNEL_FEATURES', 'leafhill', 'standard/intel/4.1.27/leaf-hill', 'standard/next', d)}"

KBRANCH_xilinx-zynq = "standard/${MACHINE}"
COMPATIBLE_MACHINE_xilinx-zynq = "${MACHINE}"

SRCREV_machine ?= "${AUTOREV}"
SRCREV_meta ?= "${AUTOREV}"

SRCREV_machine_xilinx-zynq = "5926bbf2019bbdc07a5a8fa131381acdeea0ba3e"
SRCREV_meta_xilinx-zynq = "f9e944e63afde7be724db248939399a4e04cc5a4"

SRCREV_machine_intel-corei7-64 = "${@bb.utils.contains('KERNEL_FEATURES', 'leafhill', '41fc98f785ad56f0df1b85ac039bd5e391697842', 'de4decd7e11b0e5a895765f88b8a471116473243', d)}"
SRCREV_meta_intel-corei7-64 = "68b5089eeffb5878a990d9eb418407c8e18a6f37" 

SRCREV_machine_fsl-ls10xx = "1ed4f983fe8cb304570bab041621c209d55c0883"
SRCREV_meta_fsl-ls10xx = "f749da75bbacb6b1669a2726eb362862e221f55e"

LINUX_VERSION_intel-corei7-64 = "${@bb.utils.contains('KERNEL_FEATURES', 'leafhill', '4.1.27', '4.1.29', d)}"

LINUX_VERSION_EXTENSION = "-pulsar-${LINUX_KERNEL_TYPE}"

KCONF_AUDIT_LEVEL = "0"

KERNEL_DEVICETREE_xilinx-zynq = "zynq-zc706.dtb \
                     zynq-zc702.dtb \
                     zynq-zc702-base-trd.dtb \
                     zynq-zed.dtb \
                     zynq-microzed-iiot.dtb \
                     zynq-picozed.dtb \
                     zynq-mini-itx-adv7511.dtb \
                     zynq-mini-itx-adv7511-pcie.dtb"

# For the fsl-ls10xx boards we need additional kernel configuration
KBRANCH_fsl-ls10xx = "standard/${MACHINE}"
COMPATIBLE_MACHINE_fsl-ls10xx = "${MACHINE}"

KERNEL_DEVICETREE_fsl-ls10xx = "ls1021a-iot.dtb"

KERNEL_EXTRA_ARGS_fsl-ls10xx += "LOADADDR=0x80008000"

#workaround the issue of the wifi missing ip address
#after a long time without no interaction on computestick.
KERNEL_MODULE_PROBECONF += "iwlmvm"
module_conf_iwlmvm = "options iwlmvm power_scheme=1"

# Combine all linux kernel modules into one rpm package
inherit kernel-module-combine
