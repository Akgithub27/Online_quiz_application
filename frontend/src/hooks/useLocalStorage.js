import { useEffect } from 'react';

export const useLocalStorage = (key, initialValue) => {
  const [value, setValue] = React.useState(() => {
    try {
      const item = window.localStorage.getItem(key);
      return item ? JSON.parse(item) : initialValue;
    } catch (error) {
      console.error(`Error reading from localStorage for key "${key}":`, error);
      return initialValue;
    }
  });

  const setStoredValue = (value) => {
    try {
      const valueToStore = value instanceof Function ? value(value) : value;
      setValue(valueToStore);
      window.localStorage.setItem(key, JSON.stringify(valueToStore));
    } catch (error) {
      console.error(`Error writing to localStorage for key "${key}":`, error);
    }
  };

  return [value, setStoredValue];
};

export const useAsync = (callback, immediate = true) => {
  const [status, setStatus] = React.useState('idle');
  const [value, setValue] = React.useState(null);
  const [error, setError] = React.useState(null);

  const execute = React.useCallback(async () => {
    setStatus('pending');
    setValue(null);
    setError(null);
    try {
      const response = await callback();
      setValue(response);
      setStatus('success');
      return response;
    } catch (error) {
      setError(error);
      setStatus('error');
    }
  }, [callback]);

  React.useEffect(() => {
    if (immediate) {
      execute();
    }
  }, [execute, immediate]);

  return { execute, status, value, error };
};
