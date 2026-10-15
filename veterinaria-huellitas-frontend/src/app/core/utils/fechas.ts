import { Reserva } from '../models/api.models';

/** Horas mínimas de anticipación para que el cliente pueda cancelar (igual que el backend). */
export const HORAS_MINIMAS_CANCELAR = 24;

const DIAS = ['dom', 'lun', 'mar', 'mié', 'jue', 'vie', 'sáb'];
const MESES = ['ene', 'feb', 'mar', 'abr', 'may', 'jun', 'jul', 'ago', 'sep', 'oct', 'nov', 'dic'];

const dos = (n: number) => String(n).padStart(2, '0');

/** "2026-10-20T10:00:00" -> "mar 20 oct 2026" */
export function formatFecha(iso: string): string {
  const d = new Date(iso);
  return `${DIAS[d.getDay()]} ${d.getDate()} ${MESES[d.getMonth()]} ${d.getFullYear()}`;
}

/** "2026-10-20T10:00:00" -> "10:00" */
export function formatHora(iso: string): string {
  const d = new Date(iso);
  return `${dos(d.getHours())}:${dos(d.getMinutes())}`;
}

/** Valor mínimo para un <input type="datetime-local"> (ahora, sin segundos). */
export function ahoraParaInput(): string {
  const d = new Date();
  return `${d.getFullYear()}-${dos(d.getMonth() + 1)}-${dos(d.getDate())}T${dos(d.getHours())}:${dos(d.getMinutes())}`;
}

/** El cliente puede cancelar si la reserva está confirmada y faltan 24 horas o más. */
export function puedeCancelar(r: Reserva, ahora = new Date()): boolean {
  if (r.estado !== 'CONFIRMADA') return false;
  const limite = new Date(r.fechaHora).getTime() - HORAS_MINIMAS_CANCELAR * 3600_000;
  return ahora.getTime() <= limite;
}

/** Una reserva confirmada cuya hora aún no llega. */
export function esProxima(r: Reserva, ahora = new Date()): boolean {
  return r.estado === 'CONFIRMADA' && new Date(r.fechaHora).getTime() > ahora.getTime();
}

/** Clave "2026-10" para agrupar por mes. */
export function claveMes(iso: string): string {
  const d = new Date(iso);
  return `${d.getFullYear()}-${dos(d.getMonth() + 1)}`;
}

export function etiquetaMes(clave: string): string {
  const [, m] = clave.split('-').map(Number);
  return MESES[m - 1];
}
