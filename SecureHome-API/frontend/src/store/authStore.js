import { create } from "zustand";
import { persist } from "zustand/middleware";

const decodeToken = (token) => {
  try {
    const payload = JSON.parse(atob(token.split(".")[1]));
    return { email: payload.sub, roles: payload.roles || [] };
  } catch {
    return null;
  }
};

const useAuthStore = create(
  persist(
    (set) => ({
      token: null,
      user: null,
      setToken: (token) => {
        const user = decodeToken(token);
        set({ token, user });
      },
      logout: () => set({ token: null, user: null }),
    }),
    { name: "securehome-auth" },
  ),
);

export default useAuthStore;
