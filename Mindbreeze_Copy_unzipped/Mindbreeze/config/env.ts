export const CURRENT_ENV = process.env.CURRENT_ENV || 'local';

export const ENV_CONFIG = {
	local: {
		baseUrl: process.env.MINDBREEZE_BASE_URL || 'http://localhost:3000'
	}
} as const;
