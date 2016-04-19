package net.ilexiconn.qubble.server.mode;

public enum ColorModes implements IColorMode {
    DARK {
        @Override
        public int getPrimaryColor() {
            return 0xFF212121;
        }

        @Override
        public int getSecondaryColor() {
            return 0xFF363636;
        }

        @Override
        public int getTertiaryColor() {
            return 0xFF464646;
        }

        @Override
        public int getPrimarySubcolor() {
            return 0xFF212121;
        }

        @Override
        public int getSecondarySubcolor() {
            return 0xFF1F1F1F;
        }

        @Override
        public int getTextColor() {
            return 0xFFFFFFFF;
        }
    },

    LIGHT {
        @Override
        public int getPrimaryColor() {
            return 0xFFCDCDCD;
        }

        @Override
        public int getSecondaryColor() {
            return 0xFFACACAC;
        }

        @Override
        public int getTertiaryColor() {
            return 0xFFECECEC;
        }

        @Override
        public int getPrimarySubcolor() {
            return 0xFFCDCDCD;
        }

        @Override
        public int getSecondarySubcolor() {
            return 0xFFC2C2C2;
        }

        @Override
        public int getTextColor() {
            return 0xFF000000;
        }
    }
}
