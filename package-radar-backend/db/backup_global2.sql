--
-- PostgreSQL database cluster dump
--

-- Started on 2026-10-29 21:03:10

\restrict 67GwbryVoLIelW1hhiJ885bddFeKCGHjryxj8AdZxjFx2Mszj2zFnpIwtOi1eJh

SET default_transaction_read_only = off;

SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;

--
-- Roles
--

CREATE ROLE platypulse_user;
ALTER ROLE platypulse_user WITH NOSUPERUSER INHERIT NOCREATEROLE NOCREATEDB LOGIN NOREPLICATION NOBYPASSRLS PASSWORD 'SCRAM-SHA-256$4096:ZbPvR9UHZ3g7biTMOYJEgg==$pEY1vp7czInlICuP+d6F5ToH/+CijQHPcNWm7z6PHrk=:Cg1GV9HiSotq1EGExI/efHAmCayE0aBrB60I95XlL1w=';
CREATE ROLE postgres;
ALTER ROLE postgres WITH SUPERUSER INHERIT CREATEROLE CREATEDB LOGIN REPLICATION BYPASSRLS PASSWORD 'SCRAM-SHA-256$4096:xsWnG6cejGgLf/9zs8lURw==$o0VxzdnwpkNi8blIEkQSDk9YptdpPJiitVnWXh2R6y0=:Bchd2RebsslndV9FEITpUEPxWkcOryzYM+mGnapwi2c=';
COMMENT ON ROLE postgres IS 'This is an admin account';

--
-- User Configurations
--








\unrestrict 67GwbryVoLIelW1hhiJ885bddFeKCGHjryxj8AdZxjFx2Mszj2zFnpIwtOi1eJh

-- Completed on 2026-10-29 21:03:11

--
-- PostgreSQL database cluster dump complete
--

