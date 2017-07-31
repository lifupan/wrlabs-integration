IMAGE_INSTALL_append = " ostree "
IMAGE_CLASSES += " image_types_ostree "
IMAGE_FSTYPES += " ostree "


# Please redefine OSTREE_REPO in order to have a persistent OSTree repo
OSTREE_REPO ?= "${DEPLOY_DIR_IMAGE}/ostree_repo"
# For UPTANE operation, OSTREE_BRANCHNAME must start with "${MACHINE}-"
OSTREE_BRANCHNAME ?= "${MACHINE}-ota"
OSTREE_OSNAME ?= "pulsar-essential"
OSTREE_INITRAMFS_IMAGE ?= "initramfs-ostree-image"

