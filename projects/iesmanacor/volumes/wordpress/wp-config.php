<?php
/**
 * The base configuration for WordPress
 *
 * The wp-config.php creation script uses this file during the installation.
 * You don't have to use the web site, you can copy this file to "wp-config.php"
 * and fill in the values.
 *
 * This file contains the following configurations:
 *
 * * Database settings
 * * Secret keys
 * * Database table prefix
 * * ABSPATH
 *
 * @link https://wordpress.org/support/article/editing-wp-config-php/
 *
 * @package WordPress
 */

// ** Database settings - You can get this info from your web host ** //
/** The name of the database for WordPress */
define( 'DB_NAME', 'wordpress' );

/** Database username */
define( 'DB_USER', 'mangel' );

/** Database password */
define( 'DB_PASSWORD', 'miquelangel' );

/** Database hostname */
define( 'DB_HOST', 'mariadb' );

/** Database charset to use in creating database tables. */
define( 'DB_CHARSET', 'utf8mb4' );

/** The database collate type. Don't change this if in doubt. */
define( 'DB_COLLATE', '' );

/**#@+
 * Authentication unique keys and salts.
 *
 * Change these to different unique phrases! You can generate these using
 * the {@link https://api.wordpress.org/secret-key/1.1/salt/ WordPress.org secret-key service}.
 *
 * You can change these at any point in time to invalidate all existing cookies.
 * This will force all users to have to log in again.
 *
 * @since 2.6.0
 */
define( 'AUTH_KEY',         'i6jA]ronj@EkKg/3y!s=K-9 JS)<l.4C=_U.Vc[(u|%bSI$LEChiw~7FZi[/*D)0' );
define( 'SECURE_AUTH_KEY',  'IqNJUtYkQJ,AbF,b=-8$nW!<kbHs5y(4G.8AC =kn5P/]/j?%bBc}z::i,==vW|E' );
define( 'LOGGED_IN_KEY',    '*x=;L9$ij&ZOv(Dc8weQs;vymVNULqdDAi$OQ=,$ D(#r6x_Ma#y+Geel_ (wW/m' );
define( 'NONCE_KEY',        '$p]55Nx65`IJTH 1=3`NKrE#d;@((&U!*MSr(9 ~gd!0K9U8Vm)wz*Je)Ky$p>ID' );
define( 'AUTH_SALT',        'bIMXS6-Be=N-,NtZf5D4q9GiQ9lB#<+n NwW(q(Hvlvnw.I4JkD>K.6Yl%|KK6zb' );
define( 'SECURE_AUTH_SALT', '80Sma*lqc^yVdot;.hj#Q8S5c|dzQl~$>yl%@@=83t?t-%2g[D%-<[&U9<Ag;xFm' );
define( 'LOGGED_IN_SALT',   '@>M,%p; _HxpTh6Bkck=+,8AsZl^,|F1!c25Z+v@[# [;pjbF?/Cu!Wi[LO3Dd9X' );
define( 'NONCE_SALT',       'R|_e)d-zotEaCWX6ci5GE1C-jKWl(iM),10jQsDZ4NeFQN<*_>H*5MIOFTN<})vR' );

/**#@-*/

/**
 * WordPress database table prefix.
 *
 * You can have multiple installations in one database if you give each
 * a unique prefix. Only numbers, letters, and underscores please!
 */
$table_prefix = 'wp_';

/**
 * For developers: WordPress debugging mode.
 *
 * Change this to true to enable the display of notices during development.
 * It is strongly recommended that plugin and theme developers use WP_DEBUG
 * in their development environments.
 *
 * For information on other constants that can be used for debugging,
 * visit the documentation.
 *
 * @link https://wordpress.org/support/article/debugging-in-wordpress/
 */
define( 'WP_DEBUG', false );

/* Add any custom values between this line and the "stop editing" line. */



/* That's all, stop editing! Happy publishing. */

/** Absolute path to the WordPress directory. */
if ( ! defined( 'ABSPATH' ) ) {
	define( 'ABSPATH', __DIR__ . '/' );
}

/** Sets up WordPress vars and included files. */
require_once ABSPATH . 'wp-settings.php';
