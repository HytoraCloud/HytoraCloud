package de.lystx.hytoracloud.driver.service.util.other;


public interface ITaskListener<T> {

    void onFailure(Throwable throwable);

    void onSuccess(T object);

    void onNull(Class<?> nulledClass);

}
